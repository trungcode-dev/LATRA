package trungdevcode.latra.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trungdevcode.latra.Entity.Warranty;

import java.util.List;

public interface WarrantyRepository extends JpaRepository<Warranty, Long> {

    // Lấy tất cả bảo hành thuộc về 1 đơn hàng
    List<Warranty> findByOrderId(Long orderId);

    // Query tra cứu theo SĐT của User HOẶC mã IMEI
    @Query("SELECT w FROM Warranty w " +
            "JOIN Order o ON w.orderId = o.id " +
            "JOIN User u ON o.userId = u.id " +
            "WHERE w.imei = :keyword OR u.phone = :keyword")
    List<Warranty> searchWarrantyByImeiOrPhone(@Param("keyword") String keyword);
}
