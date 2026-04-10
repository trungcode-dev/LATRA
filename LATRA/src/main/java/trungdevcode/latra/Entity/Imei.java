package trungdevcode.latra.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "imeis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Imei {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imei_code", unique = true, nullable = false)
    private String imeiCode;

    @Column(nullable = false)
    private String status = "AVAILABLE";

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}