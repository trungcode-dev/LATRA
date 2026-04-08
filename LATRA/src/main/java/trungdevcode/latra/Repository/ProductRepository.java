package trungdevcode.latra.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trungdevcode.latra.Entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Lấy danh sách sản phẩm (Bắt buộc status = 1), tích hợp Lọc Hãng, Khoảng Giá, RAM
    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN p.brand b " +
            "JOIN p.variants pv " +
            "WHERE p.status = 1 " +
            "AND (:brandName IS NULL OR b.name = :brandName) " +
            "AND (:minPrice IS NULL OR pv.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR pv.price <= :maxPrice) " +
            "AND (:ram IS NULL OR pv.storage LIKE %:ram%)")
    List<Product> searchActiveProducts(
            @Param("brandName") String brandName,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("ram") String ram);
}
