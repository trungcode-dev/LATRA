package trungdevcode.latra.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

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
    @JsonIgnore
    private Order order;

    // THÊM ĐOẠN NÀY VÀO ĐỂ LẤY NGÀY GIỜ NHẬP KHO
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}