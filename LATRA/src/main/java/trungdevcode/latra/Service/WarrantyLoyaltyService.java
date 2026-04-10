package trungdevcode.latra.Service;

import trungdevcode.latra.Dto.WarrantyResponseDTO;
import trungdevcode.latra.Entity.Order;
import trungdevcode.latra.Entity.User;
import trungdevcode.latra.Entity.Warranty;
import trungdevcode.latra.Repository.OrderRepository;
import trungdevcode.latra.Repository.UserRepository;
import trungdevcode.latra.Repository.WarrantyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class WarrantyLoyaltyService {

    @Autowired
    private WarrantyRepository warrantyRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * NHIỆM VỤ 1 & 3: KÍCH HOẠT BẢO HÀNH VÀ CỘNG ĐIỂM VIP
     * (Hàm này sẽ được gọi khi đơn hàng chuyển status thành "Hoàn thành")
     */
    @Transactional
    public void triggerPostSaleServices(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy Đơn hàng: " + orderId));

        // Validate: Phải là đơn Hoàn thành mới được kích hoạt
        if (!"Hoàn thành".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalArgumentException("Đơn hàng chưa Hoàn thành, không thể kích hoạt bảo hành!");
        }

        // 1. KÍCH HOẠT BẢO HÀNH (Đúng công thức Leader giao)
        List<Warranty> warranties = warrantyRepository.findByOrderId(orderId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusMonths(12);

        for (Warranty w : warranties) {
            w.setStartDate(now);
            w.setEndDate(endDate);
            w.setStatus("ACTIVE");
        }
        warrantyRepository.saveAll(warranties);

        // 2. CỘNG ĐIỂM TÍCH LŨY (LOYALTY)
        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy Khách hàng!"));

        // Công thức VIP: 100k = 1 điểm tích lũy
        int pointsEarned = order.getTotalAmount().divide(new BigDecimal("100000")).intValue();

        // Tránh null nếu user cũ chưa có điểm
        int currentPoints = (user.getLoyaltyPoints() == null) ? 0 : user.getLoyaltyPoints();
        user.setLoyaltyPoints(currentPoints + pointsEarned);

        userRepository.save(user);
    }

    /**
     * NHIỆM VỤ 2: TRA CỨU BẢO HÀNH
     */
    public List<WarrantyResponseDTO> lookupWarranty(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập SĐT hoặc mã IMEI để tra cứu!");
        }

        List<Warranty> results = warrantyRepository.searchWarrantyByImeiOrPhone(keyword.trim());

        if (results.isEmpty()) {
            throw new NoSuchElementException("Không tìm thấy thông tin bảo hành cho số điện thoại/IMEI này!");
        }

        return results.stream().map(w -> {
            WarrantyResponseDTO dto = new WarrantyResponseDTO();
            // Fetch User & Order to fill data
            Order o = orderRepository.findById(w.getOrderId()).orElse(null);
            if (o != null) {
                User u = userRepository.findById(o.getUserId()).orElse(null);
                dto.setCustomerName(u != null ? u.getFullName() : "Khách ẩn danh");
                dto.setPhone(u != null ? u.getPhone() : "");
            }
            dto.setImei(w.getImei());
            dto.setProductName("Sản phẩm ID: " + w.getVariantId()); // (Thực tế nên móc thêm Product name vào đây)
            dto.setStartDate(w.getStartDate());
            dto.setEndDate(w.getEndDate());

            // Check nếu quá hạn thì update status luôn trên View
            if (w.getEndDate() != null && LocalDateTime.now().isAfter(w.getEndDate())) {
                dto.setStatus("EXPIRED (Đã hết hạn)");
            } else {
                dto.setStatus(w.getStatus());
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
