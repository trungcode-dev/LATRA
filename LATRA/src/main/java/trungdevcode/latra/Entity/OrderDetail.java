package trungdevcode.latra.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Entity
@Table(name = "order_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderDetail {
    @EmbeddedId
    private OrderDetailKey id = new OrderDetailKey(); // Phải khởi tạo để tránh Null

    @ManyToOne
    @MapsId("orderId") // Ánh xạ vào trường orderId trong Key
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @MapsId("variantId") // Ánh xạ vào trường variantId trong Key
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    private Integer quantity;
    private BigDecimal price;
}