package trungdevcode.latra.Dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutRequestDTO {
    private Long userId;
    private BigDecimal totalAmount;
    private List<String> scannedImeis;

    private String customerName;
    private String customerPhone;
    private String note;

    // ==========================================
    // HỨNG DỮ LIỆU THANH TOÁN TỪ VUE GỬI XUỐNG
    // ==========================================
    private String paymentMethod;
    private BigDecimal cashAmount;
    private BigDecimal transferAmount;
}