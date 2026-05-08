package trungdevcode.latra.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import trungdevcode.latra.Dto.DashboardResponseDTO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public DashboardResponseDTO getDashboardData(String timeRange) {
        DashboardResponseDTO response = new DashboardResponseDTO();
        LocalDateTime startDate = calculateStartDate(timeRange);
        Timestamp startTimestamp = Timestamp.valueOf(startDate);

        // 1. LẤY SỐ LIỆU KPI
        String kpiSql = "SELECT " +
                "SUM(CASE WHEN status = 'COMPLETED' THEN total_amount ELSE 0 END) as totalRev, " +
                "COUNT(id) as totalOrders, " +
                "COUNT(DISTINCT user_id) as totalCustomers " +
                "FROM orders WHERE created_at >= ?";

        Map<String, Object> kpiData = jdbcTemplate.queryForMap(kpiSql, startTimestamp);

        // BỌC THÉP ÉP KIỂU: Không dùng (Long) hay (BigDecimal) trực tiếp nữa để chống lỗi 500
        Object revObj = kpiData.get("totalRev");
        BigDecimal totalRev = revObj != null ? new BigDecimal(revObj.toString()) : BigDecimal.ZERO;

        Object ordersObj = kpiData.get("totalOrders");
        long totalOrders = ordersObj != null ? ((Number) ordersObj).longValue() : 0L;

        Object customersObj = kpiData.get("totalCustomers");
        long totalCustomers = customersObj != null ? ((Number) customersObj).longValue() : 0L;

        String soldSql = "SELECT SUM(od.quantity) as sold FROM order_details od " +
                "JOIN orders o ON od.order_id = o.id WHERE o.status = 'COMPLETED' AND o.created_at >= ?";
        Number productsSoldNum = jdbcTemplate.queryForObject(soldSql, Number.class, startTimestamp);
        long productsSold = productsSoldNum != null ? productsSoldNum.longValue() : 0L;

        List<DashboardResponseDTO.KpiDTO> kpis = new ArrayList<>();
        kpis.add(new DashboardResponseDTO.KpiDTO("TỔNG DOANH THU", formatMoney(totalRev), "bi-wallet2", true, 12.5));
        kpis.add(new DashboardResponseDTO.KpiDTO("ĐƠN HÀNG MỚI", String.valueOf(totalOrders), "bi-receipt-cutoff", true, 5.0));
        kpis.add(new DashboardResponseDTO.KpiDTO("KHÁCH HÀNG", String.valueOf(totalCustomers), "bi-people-fill", true, 8.2));
        kpis.add(new DashboardResponseDTO.KpiDTO("SẢN PHẨM ĐÃ BÁN", String.valueOf(productsSold), "bi-box-seam", true, 15.4));
        response.setKpi(kpis);

        // 2. LẤY TOP SẢN PHẨM BÁN CHẠY NHẤT
        String topProductSql = "SELECT TOP 5 p.name, " +
                "(SELECT TOP 1 image_url FROM product_images WHERE product_id = p.id ORDER BY is_main DESC, id ASC) as image, " +
                "SUM(od.quantity) as soldCount, " +
                "SUM(od.quantity * od.price) as revenue " +
                "FROM order_details od " +
                "JOIN orders o ON od.order_id = o.id " +
                "JOIN product_variants v ON od.variant_id = v.id " +
                "JOIN products p ON v.product_id = p.id " +
                "WHERE o.status = 'COMPLETED' AND o.created_at >= ? " +
                "GROUP BY p.id, p.name " +
                "ORDER BY soldCount DESC";

        List<DashboardResponseDTO.ProductRankDTO> topProducts = jdbcTemplate.query(topProductSql, (rs, rowNum) -> {
            DashboardResponseDTO.ProductRankDTO dto = new DashboardResponseDTO.ProductRankDTO();
            dto.setName(rs.getString("name"));
            // Lấy ảnh gốc hoặc ảnh mặc định nếu null
            String imgUrl = rs.getString("image");
            dto.setImage(imgUrl != null ? "http://localhost:8080/uploads/" + imgUrl : "");
            dto.setSoldCount(rs.getInt("soldCount"));
            dto.setRevenue(rs.getBigDecimal("revenue"));
            return dto;
        }, startTimestamp);
        response.setProducts(topProducts);

        // 3. TẠO DỮ LIỆU BIỂU ĐỒ DOANH THU
        List<String> categories = new ArrayList<>();
        List<BigDecimal> chartData = new ArrayList<>();

        if ("7days".equals(timeRange) || "thisMonth".equals(timeRange)) {
            int days = "7days".equals(timeRange) ? 7 : 30;
            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                categories.add(date.format(DateTimeFormatter.ofPattern("dd/MM")));

                String chartSql = "SELECT SUM(total_amount) FROM orders WHERE status = 'COMPLETED' AND CAST(created_at AS DATE) = ?";
                BigDecimal dayRev = jdbcTemplate.queryForObject(chartSql, BigDecimal.class, java.sql.Date.valueOf(date));
                chartData.add(dayRev != null ? dayRev : BigDecimal.ZERO);
            }
        } else {
            categories.add("Hôm nay");
            chartData.add(totalRev);
        }

        response.setChartCategories(categories);
        response.setChartData(chartData);

        return response;
    }

    private LocalDateTime calculateStartDate(String timeRange) {
        LocalDateTime now = LocalDateTime.now();
        switch (timeRange) {
            case "today": return now.toLocalDate().atStartOfDay();
            case "thisMonth": return now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            case "thisYear": return now.withDayOfYear(1).toLocalDate().atStartOfDay();
            default: return now.minusDays(6).toLocalDate().atStartOfDay();
        }
    }

    private String formatMoney(BigDecimal value) {
        if (value.compareTo(new BigDecimal("1000000000")) >= 0) return String.format("%.1f Tỷ", value.doubleValue() / 1000000000);
        if (value.compareTo(new BigDecimal("1000000")) >= 0) return String.format("%.1fM", value.doubleValue() / 1000000);
        return value.toString();
    }
}