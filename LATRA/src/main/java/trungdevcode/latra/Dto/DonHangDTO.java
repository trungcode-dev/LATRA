package trungdevcode.latra.Dto;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DonHangDTO {

    @Data
    public static class DanhSach {
        private Long id;
        private String customerName;
        private BigDecimal totalAmount;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    public static class ChiTiet {
        private Long id;
        private String status;
        private BigDecimal totalAmount;
        private String customerName;
        private String customerPhone;
        private String shippingAddress;
        private List<Item> items;
    }

    @Data
    public static class Item {
        private Long variantId;
        private String productName;
        private String color;
        private String storage;
        private Integer quantity;
        private BigDecimal price;
    }

    @Data
    public static class CapNhatTrangThai {
        private String status;
    }
}
