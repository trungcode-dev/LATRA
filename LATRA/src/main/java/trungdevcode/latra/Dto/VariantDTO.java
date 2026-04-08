package trungdevcode.latra.Dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VariantDTO {
    private Long id;
    private String sku;
    private String color;
    private String storage;
    private BigDecimal price;
    private Integer stock;
}
