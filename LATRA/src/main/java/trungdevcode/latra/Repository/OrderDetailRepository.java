package trungdevcode.latra.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trungdevcode.latra.Entity.OrderDetail;
import trungdevcode.latra.Entity.OrderDetailKey;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailKey> {
}