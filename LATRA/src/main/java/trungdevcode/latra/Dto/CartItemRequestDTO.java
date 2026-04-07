package trungdevcode.latra.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDTO {
    @NotNull(message = "Vui lòng truyền mã phiên bản (variantId)")
    private Long variantId;

    @NotNull(message = "Vui lòng nhập số lượng")
    @Min(value = 1, message = "Số lượng tối thiểu phải là 1")
    private Integer quantity;
}
