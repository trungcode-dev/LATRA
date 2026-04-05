package trungdevcode.latra.Dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderHistoryDTO {
    private Long orderId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}
