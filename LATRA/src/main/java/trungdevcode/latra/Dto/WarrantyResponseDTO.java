package trungdevcode.latra.Dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WarrantyResponseDTO {
    private String customerName;
    private String phone;
    private String imei;
    private String productName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}
