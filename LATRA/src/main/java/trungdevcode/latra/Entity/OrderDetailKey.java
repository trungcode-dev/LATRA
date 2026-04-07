package trungdevcode.latra.Entity;


import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class OrderDetailKey implements Serializable {
    private Long orderId;   // Kiểu Long đơn giản
    private Long variantId; // Kiểu Long đơn giản
}
