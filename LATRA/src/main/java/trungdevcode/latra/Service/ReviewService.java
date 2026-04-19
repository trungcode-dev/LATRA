package trungdevcode.latra.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import trungdevcode.latra.Dto.ReviewRequestDTO;
import trungdevcode.latra.Dto.ReviewResponseDTO;
import trungdevcode.latra.Entity.Review;
import trungdevcode.latra.Repository.ReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // Hàm 1: Thêm bình luận mới
    public ReviewResponseDTO addReview(ReviewRequestDTO requestDTO) {
        Review review = Review.builder()
                .productId(requestDTO.getProductId())
                .userId(requestDTO.getUserId())
                .rating(requestDTO.getRating())
                .comment(requestDTO.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewResponseDTO.builder()
                .id(savedReview.getId())
                .rating(savedReview.getRating())
                .comment(savedReview.getComment())
                .createdAt(savedReview.getCreatedAt())
                .userName("User " + savedReview.getUserId()) // Tạm mock tên, sau ông join bảng User để lấy tên thật
                .build();
    }

    // Hàm 2: Lấy danh sách bình luận theo sản phẩm
    public List<ReviewResponseDTO> getReviewsByProduct(Long productId) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);

        return reviews.stream().map(review -> ReviewResponseDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .userName("User " + review.getUserId()) // Tạm mock
                .build()
        ).collect(Collectors.toList());
    }
}
