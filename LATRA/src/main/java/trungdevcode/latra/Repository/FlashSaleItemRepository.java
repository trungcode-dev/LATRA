package trungdevcode.latra.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trungdevcode.latra.Entity.FlashSaleItem;

import java.time.LocalDateTime;

public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {

    // Câu lệnh kiểm tra xem Phiên bản này có đang nằm trong đợt Sale nào bị TRÙNG GIỜ không
    @Query("SELECT COUNT(fsi) FROM FlashSaleItem fsi " +
            "WHERE fsi.productVariant.id = :variantId " +
            "AND fsi.flashSale.id != :excludeSaleId " + // Bỏ qua chính nó (dùng khi Update)
            "AND fsi.flashSale.startTime < :newEndTime " +
            "AND fsi.flashSale.endTime > :newStartTime")
    long countOverlappingSales(@Param("variantId") Long variantId,
                               @Param("newStartTime") LocalDateTime newStartTime,
                               @Param("newEndTime") LocalDateTime newEndTime,
                               @Param("excludeSaleId") Long excludeSaleId);
}