package trungdevcode.latra.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.Entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}