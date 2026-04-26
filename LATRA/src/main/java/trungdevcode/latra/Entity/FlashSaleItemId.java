package trungdevcode.latra.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleItemId implements Serializable {

    @Column(name = "flash_sale_id")
    private Long flashSaleId;

    @Column(name = "variant_id")
    private Long variantId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlashSaleItemId that = (FlashSaleItemId) o;
        return Objects.equals(flashSaleId, that.flashSaleId) &&
                Objects.equals(variantId, that.variantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flashSaleId, variantId);
    }
}