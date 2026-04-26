package trungdevcode.latra.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Data
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "discount_type", nullable = false)
    private String discountType;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(name = "usage_limit", nullable = false)
    private Integer usageLimit;

    @Column(name = "min_order_value", nullable = false)
    private BigDecimal minOrderValue;

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;
}