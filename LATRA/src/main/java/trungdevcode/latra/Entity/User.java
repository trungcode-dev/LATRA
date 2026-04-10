package trungdevcode.latra.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0; // Điểm tích lũy VIP

    private String username;
    private String password;

    @Column(name = "full_name")
    private String fullName;
    private String email;
    private String phone;
    private String address;

    // 1: active, 0: locked
    private Integer status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Quan hệ Many-To-Many với bảng roles qua bảng trung gian user_role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
