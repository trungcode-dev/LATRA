package trungdevcode.latra.Dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutRequestDTO {
    // ID của khách hàng hoặc nhân viên đang thao tác (Tùy logic bạn chọn)
    private Long userId;

    // Tổng số tiền khách phải trả
    private BigDecimal totalAmount;

    // Danh sách các mã IMEI vừa dùng súng quét được
    private List<String> scannedImeis;
}