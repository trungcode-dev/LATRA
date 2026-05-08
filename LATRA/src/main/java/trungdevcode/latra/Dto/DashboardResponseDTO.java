package trungdevcode.latra.Dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardResponseDTO {
    private List<KpiDTO> kpi;
    private List<String> chartCategories;
    private List<BigDecimal> chartData;
    private List<ProductRankDTO> products;

    @Data
    public static class KpiDTO {
        private String title;
        private String value;
        private String icon;
        private boolean isUp;
        private double percent;

        public KpiDTO(String title, String value, String icon, boolean isUp, double percent) {
            this.title = title;
            this.value = value;
            this.icon = icon;
            this.isUp = isUp;
            this.percent = percent;
        }
    }

    @Data
    public static class ProductRankDTO {
        private String name;
        private String image;
        private Integer soldCount;
        private BigDecimal revenue;
    }
}