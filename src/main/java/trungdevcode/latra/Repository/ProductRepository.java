package trungdevcode.latra.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.Entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
