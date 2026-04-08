package trungdevcode.latra.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungdevcode.latra.Dto.ProductDetailDTO;
import trungdevcode.latra.Dto.ProductListDTO;
import trungdevcode.latra.Dto.VariantDTO;
import trungdevcode.latra.Entity.Product;
import trungdevcode.latra.Entity.ProductImage;
import trungdevcode.latra.Entity.ProductVariant;
import trungdevcode.latra.Repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ProductDisplayService {

    @Autowired
    private ProductRepository productRepository;

    // 1 & 2. API Danh sách & Lọc (CHỈ LẤY STATUS = 1)
    public List<ProductListDTO> getProductsForDisplay(String brand, BigDecimal minPrice, BigDecimal maxPrice, String ram) {

        // --- VALIDATE LOGIC ---
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá thấp nhất không được nhỏ hơn 0!");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Khoảng giá không hợp lệ (Giá min đang lớn hơn giá max)!");
        }

        List<Product> products = productRepository.searchActiveProducts(brand, minPrice, maxPrice, ram);

        return products.stream().map(p -> {
            ProductListDTO dto = new ProductListDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setBrandName(p.getBrand().getName());

            // Lấy giá thấp nhất trong các phiên bản để hiển thị "Giá từ: ..."
            BigDecimal startingPrice = p.getVariants().stream()
                    .map(ProductVariant::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            dto.setStartingPrice(startingPrice);

            // Lấy ảnh chính (isMain = true)
            String mainImage = p.getImages().stream()
                    .filter(ProductImage::getIsMain)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse("https://via.placeholder.com/300"); // Ảnh mặc định nếu thiếu
            dto.setMainImageUrl(mainImage);

            return dto;
        }).collect(Collectors.toList());
    }

    // 3. API Xem chi tiết 1 máy
    public ProductDetailDTO getProductDetail(Long productId) {
        // --- VALIDATE LOGIC ---
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ!");
        }

        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy sản phẩm với ID: " + productId));

        // Bắt buộc sản phẩm phải đang active (status = 1) mới cho xem
        if (p.getStatus() != 1) {
            throw new NoSuchElementException("Sản phẩm này đã ngừng kinh doanh hoặc đang bị ẩn!");
        }

        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setBrandName(p.getBrand().getName());
        dto.setCategoryName(p.getCategory().getName());
        dto.setDescription(p.getDescription());

        // Map Danh sách ảnh
        dto.setImageUrls(p.getImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList()));

        // Map Danh sách biến thể (RAM, ROM, MÀU)
        List<VariantDTO> variantDTOs = p.getVariants().stream().map(v -> {
            VariantDTO vDto = new VariantDTO();
            vDto.setId(v.getId());
            vDto.setSku(v.getSku());
            vDto.setColor(v.getColor());
            vDto.setStorage(v.getStorage());
            vDto.setPrice(v.getPrice());
            vDto.setStock(v.getStock());
            return vDto;
        }).collect(Collectors.toList());

        dto.setVariants(variantDTOs);

        return dto;
    }
}
