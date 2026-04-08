package trungdevcode.latra.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.ProductDetailDTO;
import trungdevcode.latra.Dto.ProductListDTO;
import trungdevcode.latra.Service.ProductDisplayService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/public/products") // public: Không cần token
@CrossOrigin("*")
public class ProductDisplayController {

    @Autowired
    private ProductDisplayService productDisplayService;

    // API 1 & 2: Lấy danh sách (Mặc định) HOẶC Lọc nhiều tiêu chí
    // VD test: GET /api/public/products
    // VD lọc:  GET /api/public/products?brandName=Apple&minPrice=10000000&ram=256GB
    @GetMapping
    public ResponseEntity<List<ProductListDTO>> getProducts(
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String ram) {

        return ResponseEntity.ok(productDisplayService.getProductsForDisplay(brandName, minPrice, maxPrice, ram));
    }

    // API 3: Xem chi tiết 1 sản phẩm kèm biến thể
    // VD test: GET /api/public/products/1
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable Long id) {
        return ResponseEntity.ok(productDisplayService.getProductDetail(id));
    }
}
