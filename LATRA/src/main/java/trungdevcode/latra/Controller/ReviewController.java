package trungdevcode.latra.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.ReviewRequestDTO;
import trungdevcode.latra.Service.ReviewService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Giải quyết luôn lỗi CORS cho Frontend
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // API 1: Khách vãng lai xem bình luận (Mở tự do)
    @GetMapping("/public/reviews/product/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    // API 2: User đăng bình luận mới (Tạm mở, sau Trung M5 làm Security xong thì thêm @PreAuthorize vào đây)
    @PostMapping("/reviews")
    public ResponseEntity<?> addReview(@Valid @RequestBody ReviewRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addReview(request));
    }
}
