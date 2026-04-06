package trungdevcode.latra.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trungdevcode.latra.Entity.OrderEntity;

// File: OrderRepository.java
@Repository
public interface OrderRepository {

    @Repository
    interface Order extends JpaRepository<OrderEntity, Long> {
        // Lọc đơn hàng theo trạng thái
        Page<OrderEntity> findByStatus(String status, Pageable pageable);
    }

    @Repository
    interface ProductVariant extends JpaRepository<OrderEntity.ProductVariant, Long> {
    }
}
