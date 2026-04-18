package trungdevcode.latra.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Entity.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByPhone(String phone);
    User findByUsername(String username);

    @Query(value = "SELECT u.* FROM users u " +
            "LEFT JOIN user_role ur ON u.id = ur.user_id " +
            "WHERE (ur.role_id = 3 OR ur.role_id IS NULL) " +
            "AND u.id NOT IN (1, 2)", nativeQuery = true)
    List<User> findAllCustomersOnly();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_role (user_id, role_id) VALUES (:userId, 3)", nativeQuery = true)
    void assignCustomerRole(@Param("userId") Long userId);
}