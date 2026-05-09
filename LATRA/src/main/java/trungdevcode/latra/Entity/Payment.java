package trungdevcode.latra.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    private String method; // VNPAY, MOMO, COD
    private String status; // SUCCESS, FAILED
    private LocalDateTime paidAt;
}
