package trungdevcode.latra.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungdevcode.latra.Entity.Order;

import java.math.BigDecimal;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByStatus(String status, Pageable pageable);
    @Query(value = "SELECT COUNT(*) FROM orders WHERE user_id = ?1 AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)", nativeQuery = true)
    int countOrdersTodayByUserId(Long userId);

    // 2. Tính tổng tiền các đơn đã hoàn thành của nhân viên trong ngày hôm nay
    @Query(value = "SELECT SUM(total_amount) FROM orders WHERE user_id = ?1 AND status = 'COMPLETED' AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)", nativeQuery = true)
    BigDecimal sumRevenueTodayByUserId(Long userId);
}
