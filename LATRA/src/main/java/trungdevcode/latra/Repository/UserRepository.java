package trungdevcode.latra.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trungdevcode.latra.Entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // Query lấy danh sách User, nếu có truyền roleName thì lọc theo role
    @Query("SELECT u FROM User u JOIN u.roles r WHERE (:roleName IS NULL OR r.name = :roleName)")
    List<User> findAllByRoleName(@Param("roleName") String roleName);
}
