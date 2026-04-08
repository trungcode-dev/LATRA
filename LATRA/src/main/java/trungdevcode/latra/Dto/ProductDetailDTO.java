package trungdevcode.latra.Dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductDetailDTO {
    private Long id;
    private String name;
    private String brandName;
    private String categoryName;
    private String description;
    private List<String> imageUrls; // Danh sách ảnh (Carousel)
    private List<VariantDTO> variants; // Các phiên bản (Màu, Storage, Giá, Tồn)
}
