package trungdevcode.latra.Dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VoucherRequestDTO {

    @NotBlank(message = "Mã Voucher không được để trống")
    private String code;

    @NotBlank(message = "Loại giảm giá không được để trống (PERCENT hoặc FIXED)")
    private String discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    private BigDecimal value;

    @NotNull(message = "Ngày hết hạn không được để trống")
    @Future(message = "Ngày hết hạn phải nằm trong tương lai")
    private LocalDateTime expiredAt;
}
