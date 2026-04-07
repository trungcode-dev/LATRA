package trungdevcode.latra.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.Entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
}