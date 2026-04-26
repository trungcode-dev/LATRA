package trungdevcode.latra.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.Entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}