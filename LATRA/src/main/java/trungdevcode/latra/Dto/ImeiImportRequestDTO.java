package trungdevcode.latra.Dto;

import java.util.List;
import lombok.Data;
@Data
public class ImeiImportRequestDTO {
    private Long variantId;
    private List<String> imeis;
}
