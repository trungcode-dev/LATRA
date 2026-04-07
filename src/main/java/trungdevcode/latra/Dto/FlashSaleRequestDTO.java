package trungdevcode.latra.Dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
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

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @Future(message = "Thời gian kết thúc phải ở tương lai")
    private LocalDateTime endTime;

    private List<ItemDTO> items;

    @Data
    public static class ItemDTO {
        @NotNull(message = "Thiếu ID sản phẩm")
        private Long productId;

        @NotNull(message = "Thiếu giá Sale")
        private BigDecimal salePrice;

        @NotNull(message = "Thiếu số lượng Sale")
        private Integer quantity;
    }
}
