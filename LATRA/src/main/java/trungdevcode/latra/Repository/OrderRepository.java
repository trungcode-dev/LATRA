package trungdevcode.latra.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.Entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Tự động generate câu SQL tìm đơn hàng theo User ID, xếp mới nhất lên đầu
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
