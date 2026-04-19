package trungdevcode.latra.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trungdevcode.latra.Entity.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Tự động generate câu query SQL: SELECT * FROM reviews WHERE product_id = ? ORDER BY created_at DESC
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
}
