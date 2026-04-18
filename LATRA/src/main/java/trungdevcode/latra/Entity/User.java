package trungdevcode.latra.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Các cột này trong SQL là VARCHAR -> Không dùng @Nationalized
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;
    private String phone;

    // Cột này trong SQL là NVARCHAR -> Dùng @Nationalized và trỏ đúng tên cột
    @Nationalized
    @Column(name = "full_name")
    private String fullName;

    // Cột này trong SQL là NVARCHAR -> Dùng @Nationalized
    @Nationalized
    private String address;

    private Integer status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}