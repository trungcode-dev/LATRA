package trungdevcode.latra.Dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ShiftSummaryDTO {
    private String staffName;
    private String startTime;
    private int totalOrders;
    private BigDecimal totalRevenue;
}