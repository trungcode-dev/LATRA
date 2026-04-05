package trungdevcode.latra.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trungdevcode.latra.Dto.ProductRequestDTO;
import trungdevcode.latra.Dto.ProductResponseDTO;
import trungdevcode.latra.Service.ProductService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductDetail(@PathVariable Long id) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO request) {

        ProductResponseDTO response = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> response = productService.getAllProducts(page, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Đã ẩn sản phẩm thành công!");
    }

    @PostMapping("/admin/{id}/images")
    public ResponseEntity<String> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String result = productService.uploadImage(id, file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi upload ảnh: " + e.getMessage());
        }
    }
}
