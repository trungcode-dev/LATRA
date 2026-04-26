package trungdevcode.latra.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "flash_sale_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleItem {

    @EmbeddedId
    private FlashSaleItemId id = new FlashSaleItemId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("flashSaleId")
    @JoinColumn(name = "flash_sale_id")
    @JsonIgnore
    private FlashSale flashSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("variantId")
    @JoinColumn(name = "variant_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ProductVariant productVariant;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "quantity_limit")
    private Integer quantityLimit;
}