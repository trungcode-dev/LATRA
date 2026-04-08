package trungdevcode.latra.Dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductListDTO {
    private Long id;
    private String name;
    private String brandName;
    private String mainImageUrl;
    private BigDecimal startingPrice; // Giá thấp nhất trong các phiên bản
}
