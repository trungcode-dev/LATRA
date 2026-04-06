package trungdevcode.latra.Entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, length = 50)
    private String code;

    @Column(name = "discount_type", length = 10)
    private String discountType;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}