package trungdevcode.latra.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.Entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}