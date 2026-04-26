package trungdevcode.latra.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailKey implements Serializable {
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "variant_id")
    private Long variantId;
}