package trungdevcode.latra.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class OrderDetailKey implements Serializable {
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "variant_id")
    private Long variantId;
}
