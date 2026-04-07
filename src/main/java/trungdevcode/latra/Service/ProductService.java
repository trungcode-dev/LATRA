package trungdevcode.latra.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import trungdevcode.latra.Dto.ProductRequestDTO;
import trungdevcode.latra.Dto.ProductResponseDTO;
import trungdevcode.latra.Entity.Product;
import trungdevcode.latra.Entity.ProductImage;
import trungdevcode.latra.Entity.ProductVariant;
import trungdevcode.latra.Repository.ProductImageRepository;
import trungdevcode.latra.Repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductResponseDTO getProductById(Long id) {
        // Tìm trong DB, nếu không có ném ra lỗi
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        // Chuyển từ Entity sang DTO
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());

        // Map list Variant Entity sang Variant DTO
        List<ProductResponseDTO.VariantDTO> variantDTOs = product.getVariants().stream().map(v -> {
            ProductResponseDTO.VariantDTO vDto = new ProductResponseDTO.VariantDTO();
            vDto.setVariantId(v.getId());
            vDto.setColor(v.getColor());
            vDto.setStorage(v.getStorage());
            vDto.setPrice(v.getPrice());
            vDto.setStock(v.getStock());
            return vDto;
        }).collect(Collectors.toList());

        dto.setVariants(variantDTOs);
        return dto;
    }
    @Transactional // RẤT QUAN TRỌNG: Đảm bảo nếu lưu Variant lỗi thì rollback (hủy) luôn việc lưu Product
    public ProductResponseDTO createProduct(ProductRequestDTO request) {

        // 1. Tạo Entity Product mới và đổ dữ liệu từ DTO sang
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrandId(request.getBrandId());
        product.setCategoryId(request.getCategoryId());
        product.setStatus(1); // Mặc định là đang bán

        // 2. Xử lý danh sách Biến thể (Variants)
        if (request.getVariants() != null) {
            List<ProductVariant> variantList = request.getVariants().stream().map(vDto -> {
                ProductVariant variant = new ProductVariant();
                variant.setColor(vDto.getColor());
                variant.setStorage(vDto.getStorage());
                variant.setPrice(vDto.getPrice());
                variant.setStock(vDto.getStock());
                variant.setSku(vDto.getSku());

                // QUAN TRỌNG NHẤT: Phải móc cái Variant này vào Product cha
                // Nếu quên dòng này, khóa ngoại product_id trong DB sẽ bị Null
                variant.setProduct(product);
                return variant;
            }).collect(Collectors.toList());

            product.setVariants(variantList);
        }

        // 3. Lưu xuống Database (Lưu 1 phát ăn luôn cả cha lẫn con)
        Product savedProduct = productRepository.save(product);

        // 4. Lấy cái ID vừa lưu, gọi lại hàm getProductById để trả kết quả đẹp đẽ ra ngoài
        return getProductById(savedProduct.getId());
    }
    public Map<String, Object> getAllProducts(int page, int size) {
        // 1. Tạo đối tượng phân trang (Spring Boot mặc định trang đầu tiên là số 0)
        Pageable paging = PageRequest.of(page, size);

        // 2. Gọi Repository lấy dữ liệu theo trang
        Page<Product> pageProducts = productRepository.findAll(paging);

        // 3. Chuyển Entity sang DTO giống y hệt hàm getProductById
        List<ProductResponseDTO> productDTOs = pageProducts.getContent().stream().map(product -> {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());

            // Map list Variant
            if (product.getVariants() != null) {
                List<ProductResponseDTO.VariantDTO> variantDTOs = product.getVariants().stream().map(v -> {
                    ProductResponseDTO.VariantDTO vDto = new ProductResponseDTO.VariantDTO();
                    vDto.setVariantId(v.getId());
                    vDto.setColor(v.getColor());
                    vDto.setStorage(v.getStorage());
                    vDto.setPrice(v.getPrice());
                    vDto.setStock(v.getStock());
                    return vDto;
                }).collect(Collectors.toList());
                dto.setVariants(variantDTOs);
            }
            return dto;
        }).collect(Collectors.toList());

        // 4. Đóng gói kết quả gửi về cho Frontend
        Map<String, Object> response = new HashMap<>();
        response.put("products", productDTOs);          // Danh sách sản phẩm của trang này
        response.put("currentPage", pageProducts.getNumber()); // Trang hiện tại
        response.put("totalItems", pageProducts.getTotalElements()); // Tổng số sản phẩm trong DB
        response.put("totalPages", pageProducts.getTotalPages());   // Tổng số trang

        return response;
    }
    // Hàm Cập nhật sản phẩm
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        // Cập nhật thông tin cơ bản
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrandId(request.getBrandId());
        product.setCategoryId(request.getCategoryId());

        // (Tạm thời mình chỉ cập nhật thông tin máy. Việc cập nhật từng Biến thể phức tạp hơn,
        // thường sẽ làm 1 API riêng hoặc xóa hết biến thể cũ thêm biến thể mới. Mình làm cơ bản trước nhé).

        productRepository.save(product);
        return getProductById(id);
    }

    // Hàm Xóa mềm (Ẩn sản phẩm)
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        // Chuyển status về 0 thay vì xóa khỏi DB
        product.setStatus(0);
        productRepository.save(product);
    }
    @Autowired
    private ProductImageRepository productImageRepository;

    public String uploadImage(Long productId, MultipartFile file) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        // 1. Tạo thư mục 'uploads' nếu chưa có
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 2. Đổi tên file để không bị trùng (VD: sdf87-image.jpg)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // 3. Lưu file vật lý vào ổ cứng máy tính
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 4. Lưu đường dẫn vào SQL Server
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(uniqueFilename); // Chỉ lưu tên file
        productImage.setIsMain(true); // Tạm set ảnh đầu tiên là ảnh chính
        productImageRepository.save(productImage);

        return "Upload thành công! Tên file: " + uniqueFilename;
    }
}
