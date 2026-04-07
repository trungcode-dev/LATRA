package trungdevcode.latra.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Thiếu Brand ID")
    private Long brandId;

    @NotNull(message = "Thiếu Category ID")
    private Long categoryId;

    private List<VariantRequestDTO> variants;

    @Data
    public static class VariantRequestDTO {
        private String color;
        private String storage;
        private BigDecimal price;
        private Integer stock;
        private String sku;
    }
}
