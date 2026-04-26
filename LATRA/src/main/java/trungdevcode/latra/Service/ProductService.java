package trungdevcode.latra.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import trungdevcode.latra.Dto.ProductRequestDTO;
import trungdevcode.latra.Dto.ProductResponseDTO;
import trungdevcode.latra.Entity.Product;
import trungdevcode.latra.Entity.ProductImage;
import trungdevcode.latra.Entity.ProductVariant;
import trungdevcode.latra.Repository.ProductImageRepository;
import trungdevcode.latra.Repository.ProductRepository;
import trungdevcode.latra.Repository.ProductVariantRepository;
import org.springframework.dao.DataIntegrityViolationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setBrandId(product.getBrandId());
        dto.setCategoryId(product.getCategoryId());
        dto.setStatus(product.getStatus());

        // Lấy ảnh chính (ưu tiên isMain = true)
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String img = product.getImages().stream()
                    .filter(ProductImage::getIsMain)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(product.getImages().get(0).getImageUrl());
            dto.setMainImageUrl(img);
        }

        List<ProductResponseDTO.VariantDTO> variantDTOs = product.getVariants().stream().map(v -> {
            ProductResponseDTO.VariantDTO vDto = new ProductResponseDTO.VariantDTO();
            vDto.setVariantId(v.getId());
            vDto.setColor(v.getColor());
            vDto.setStorage(v.getStorage());
            vDto.setPrice(v.getPrice());
            vDto.setStock(v.getStock());
            vDto.setSku(v.getSku());
            return vDto;
        }).collect(Collectors.toList());

        dto.setVariants(variantDTOs);
        return dto;
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        // --- VALIDATION: Kiểm tra trùng mã SKU trong danh sách gửi lên (Front-end) ---
        validateUniqueSkus(request.getVariants());

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrandId(request.getBrandId());
        product.setCategoryId(request.getCategoryId());
        product.setStatus(request.getStatus());

        if (request.getVariants() != null) {
            List<ProductVariant> variantList = request.getVariants().stream().map(vDto -> {
                ProductVariant variant = new ProductVariant();
                variant.setColor(vDto.getColor());
                variant.setStorage(vDto.getStorage());
                variant.setPrice(vDto.getPrice()); // BigDecimal
                variant.setStock(vDto.getStock());
                variant.setSku(vDto.getSku());
                variant.setProduct(product);
                return variant;
            }).collect(Collectors.toList());

            product.setVariants(variantList);
        }

        // --- BẮT LỖI DATABASE: Trùng SKU với sản phẩm khác đã có trong DB ---
        try {
            // Dùng saveAndFlush để ép Spring Boot ghi ngay lập tức và bắt lỗi liền tay
            Product savedProduct = productRepository.saveAndFlush(product);
            return getProductById(savedProduct.getId());
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể lưu! Mã SKU bạn nhập đã bị trùng với một sản phẩm khác trong hệ thống.");
        }
    }

    public Map<String, Object> getAllProducts(int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Product> pageProducts = productRepository.findAll(paging);

        List<ProductResponseDTO> productDTOs = pageProducts.getContent().stream().map(product -> {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setBrandId(product.getBrandId());
            dto.setCategoryId(product.getCategoryId());
            dto.setStatus(product.getStatus());

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                String img = product.getImages().stream()
                        .filter(ProductImage::getIsMain)
                        .map(ProductImage::getImageUrl)
                        .findFirst()
                        .orElse(product.getImages().get(0).getImageUrl());
                dto.setMainImageUrl(img);
            }

            if (product.getVariants() != null) {
                List<ProductResponseDTO.VariantDTO> variantDTOs = product.getVariants().stream().map(v -> {
                    ProductResponseDTO.VariantDTO vDto = new ProductResponseDTO.VariantDTO();
                    vDto.setVariantId(v.getId());
                    vDto.setColor(v.getColor());
                    vDto.setStorage(v.getStorage());
                    vDto.setPrice(v.getPrice());
                    vDto.setStock(v.getStock());
                    vDto.setSku(v.getSku());
                    return vDto;
                }).collect(Collectors.toList());
                dto.setVariants(variantDTOs);
            }
            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("products", productDTOs);
        response.put("currentPage", pageProducts.getNumber());
        response.put("totalItems", pageProducts.getTotalElements());
        response.put("totalPages", pageProducts.getTotalPages());

        return response;
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));

        // --- VALIDATION: Kiểm tra trùng mã SKU trong danh sách gửi lên ---
        validateUniqueSkus(request.getVariants());

        // Cập nhật thông tin Vỏ sản phẩm
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrandId(request.getBrandId());
        product.setCategoryId(request.getCategoryId());
        product.setStatus(request.getStatus());

        // --- LOGIC MỚI: Cập nhật Variant thông minh (Update in-place) ---
        // 1. Nhóm các phiên bản gửi lên từ Vue.js vào một Map (Dùng SKU làm chìa khóa)
        Map<String, ProductRequestDTO.VariantRequestDTO> requestMap = new HashMap<>();
        if (request.getVariants() != null) {
            for (var v : request.getVariants()) {
                if (v.getSku() != null && !v.getSku().trim().isEmpty()) {
                    requestMap.put(v.getSku().trim().toUpperCase(), v);
                }
            }
        }

        // 2. Duyệt qua các phiên bản ĐANG CÓ trong Database
        Iterator<ProductVariant> iterator = product.getVariants().iterator();
        while (iterator.hasNext()) {
            ProductVariant existingVariant = iterator.next();
            String existingSku = existingVariant.getSku().toUpperCase();

            if (requestMap.containsKey(existingSku)) {
                // Nếu SKU cũ có mặt trong danh sách gửi lên -> CẬP NHẬT thông tin
                var vDto = requestMap.get(existingSku);
                existingVariant.setColor(vDto.getColor());
                existingVariant.setStorage(vDto.getStorage());
                existingVariant.setPrice(vDto.getPrice());
                // TUYỆT ĐỐI KHÔNG cập nhật Tồn kho (Stock) ở đây, giữ nguyên số của Database

                // Xong việc thì xóa khỏi Map để lát nữa xử lý bọn thêm mới
                requestMap.remove(existingSku);
            } else {
                // Nếu SKU cũ KHÔNG có mặt trong danh sách gửi lên -> XÓA (Admin đã bấm nút thùng rác)
                // Lưu ý: Nếu phiên bản này đã có IMEI, DB sẽ văng lỗi Khóa ngoại để bảo vệ dữ liệu!
                iterator.remove();
            }
        }

        // 3. Những SKU còn sót lại trong Map chính là những phiên bản THÊM MỚI
        for (var vDto : requestMap.values()) {
            ProductVariant newVariant = new ProductVariant();
            newVariant.setColor(vDto.getColor());
            newVariant.setStorage(vDto.getStorage());
            newVariant.setPrice(vDto.getPrice());
            newVariant.setStock(0); // Hàng mới mặc định tồn kho = 0
            newVariant.setSku(vDto.getSku().toUpperCase());
            newVariant.setProduct(product);
            product.getVariants().add(newVariant);
        }

        // --- LƯU XUỐNG DATABASE ---
        try {
            productRepository.saveAndFlush(product);
            return getProductById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể cập nhật! Có thể mã SKU bị trùng với sản phẩm khác, hoặc bạn đang cố xóa một phiên bản đã có IMEI trong kho.");
        }
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));

        product.setStatus(0); // Xóa mềm (Soft Delete)
        productRepository.save(product);
    }

    public String uploadImage(Long productId, MultipartFile file) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));

        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(uniqueFilename);

        boolean hasMainImage = product.getImages() != null && !product.getImages().isEmpty();
        productImage.setIsMain(!hasMainImage);

        productImageRepository.save(productImage);

        return "Upload thành công! Tên file: " + uniqueFilename;
    }

    /**
     * Hàm helper để kiểm tra trùng lặp SKU trong danh sách truyền lên
     */
    private void validateUniqueSkus(List<ProductRequestDTO.VariantRequestDTO> variants) {
        if (variants == null || variants.isEmpty()) return;

        List<String> skuList = variants.stream()
                .map(ProductRequestDTO.VariantRequestDTO::getSku)
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toList());

        Set<String> uniqueSkus = new HashSet<>(skuList);

        if (uniqueSkus.size() < skuList.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể lưu! Có mã SKU bị trùng lặp trong danh sách phiên bản.");
        }
    }
}