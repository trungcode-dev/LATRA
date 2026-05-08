package trungdevcode.latra.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 150, message = "Tên sản phẩm không được vượt quá 150 ký tự")
    private String name;

    private String description;

    @NotNull(message = "Thiếu Brand ID")
    private Long brandId;

    @NotNull(message = "Thiếu Category ID")
    private Long categoryId;

    @NotNull(message = "Thiếu trạng thái sản phẩm")
    private Integer status;
    @Valid
    @NotEmpty(message = "Sản phẩm phải có ít nhất 1 phiên bản")
    private List<VariantRequestDTO> variants;

    @Data
    public static class VariantRequestDTO {
        private Long id;

        @NotBlank(message = "Màu sắc không được để trống")
        private String color;

        private String storage;

        // 👉 THÊM DÒNG NÀY VÀO ĐÂY:
        private String condition;

        @NotNull(message = "Giá không được để trống")
        @DecimalMin(value = "0.0", inclusive = true, message = "Giá không được âm")
        private BigDecimal price;

        @NotNull(message = "Tồn kho không được để trống")
        @Min(value = 0, message = "Tồn kho không được âm")
        private Integer stock;

        @NotBlank(message = "Mã SKU không được để trống")
        private String sku;
    }
}
