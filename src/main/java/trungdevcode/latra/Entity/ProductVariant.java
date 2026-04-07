package trungdevcode.latra.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "color", columnDefinition = "NVARCHAR(50)")
    private String color;

    @Column(name = "storage", columnDefinition = "NVARCHAR(50)")
    private String storage;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "stock")
    private Integer stock = 0;

    @Column(name = "sku", unique = true)
    private String sku;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
