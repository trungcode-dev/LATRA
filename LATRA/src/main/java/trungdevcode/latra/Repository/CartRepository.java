package trungdevcode.latra.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.Entity.Cart;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}