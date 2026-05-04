package trungdevcode.latra.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Entity.User;
import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    User findByPhone(String phone);
    User findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);

    @Query(value = "SELECT u.* FROM users u " +
            "LEFT JOIN user_role ur ON u.id = ur.user_id " +
            "WHERE (ur.role_id = 3 OR ur.role_id IS NULL) " +
            "AND u.status != 2 ORDER BY u.id DESC", nativeQuery = true)
    List<User> findAllCustomersOnly();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_role (user_id, role_id) VALUES (:userId, 3)", nativeQuery = true)
    void assignCustomerRole(@Param("userId") Long userId);

    // Sửa DESC thành ASC để sắp xếp từ cũ nhất đến mới nhất (NV001 -> NV012...)
    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_role ur ON u.id = ur.user_id " +
            "WHERE ur.role_id IN (1, 2) AND u.status != 2 ORDER BY u.id ASC", nativeQuery = true)
    List<User> findAllEmployeesOnly();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_role (user_id, role_id) VALUES (:userId, :roleId)", nativeQuery = true)
    void assignRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user_role SET role_id = :roleId WHERE user_id = :userId", nativeQuery = true)
    void updateUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Query(value = "SELECT TOP 1 role_id FROM user_role WHERE user_id = :userId", nativeQuery = true)
    Long findRoleIdByUserId(@Param("userId") Long userId);
}