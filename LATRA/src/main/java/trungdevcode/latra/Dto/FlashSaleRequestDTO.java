package trungdevcode.latra.Dto;

import jakarta.validation.Valid; // BẮT BUỘC PHẢI IMPORT CÁI NÀY
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty; // BẮT BUỘC PHẢI IMPORT CÁI NÀY
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FlashSaleRequestDTO {

    @NotBlank(message = "Tên chương trình không được để trống")
    private String name;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startTime;

    // FlashSaleRequestDTO.java
    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime endTime;

    // --- 2 DÒNG ĐÃ SỬA LỖI NẰM Ở ĐÂY ---
    @Valid // Dòng này ÉP Spring Boot chui vào trong ItemDTO để quét lỗi @Min
    @NotEmpty(message = "Chương trình Flash Sale phải có ít nhất 1 sản phẩm") // Dòng này ép không được gửi mảng rỗng []
    private List<ItemDTO> items;

    @Data
    public static class ItemDTO {
        @NotNull(message = "Thiếu ID phiên bản sản phẩm")
        private Long variantId;

        @NotNull(message = "Thiếu giá Sale")
        @Min(value = 0, message = "Giá Sale không được là số âm")
        private BigDecimal salePrice;

        @NotNull(message = "Thiếu số lượng giới hạn")
        @Min(value = 1, message = "Số lượng Sale phải ít nhất là 1")
        private Integer quantityLimit;
    }
}