package trungdevcode.latra.Entity;


import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@Data
public class OrderDetail {
    @EmbeddedId
    private OrderDetailKey id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne
    @MapsId("variantId")
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    private Integer quantity;
    private BigDecimal price;
}
