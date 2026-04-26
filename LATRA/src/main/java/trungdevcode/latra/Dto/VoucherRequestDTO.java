package trungdevcode.latra.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VoucherRequestDTO {

    @NotBlank(message = "Mã voucher không được để trống")
    private String code;

    @NotBlank(message = "Thiếu loại giảm giá")
    private String discountType;

    @NotNull(message = "Thiếu giá trị giảm")
    @Min(value = 1, message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal value;

    @NotNull(message = "Thiếu giới hạn sử dụng")
    @Min(value = 1, message = "Phải có ít nhất 1 lượt sử dụng")
    private Integer usageLimit;

    @NotNull(message = "Thiếu giá trị đơn tối thiểu")
    @Min(value = 0, message = "Đơn tối thiểu không được âm")
    private BigDecimal minOrderValue;

    private BigDecimal maxDiscountAmount;

    @NotNull(message = "Thiếu ngày bắt đầu")
    private LocalDateTime startDate;

    @NotNull(message = "Thiếu ngày kết thúc")
    private LocalDateTime expiredAt;
}