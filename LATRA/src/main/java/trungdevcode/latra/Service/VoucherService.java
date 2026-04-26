package trungdevcode.latra.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungdevcode.latra.Dto.VoucherRequestDTO;
import trungdevcode.latra.Entity.Voucher;
import trungdevcode.latra.Repository.VoucherRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    private void mapDtoToEntity(Voucher voucher, VoucherRequestDTO request, boolean isUpdate) {
        LocalDateTime now = LocalDateTime.now();

        // 1. Logic thời gian
        if (!request.getStartDate().isBefore(request.getExpiredAt())) {
            throw new RuntimeException("Ngày bắt đầu phải xảy ra trước ngày kết thúc!");
        }
        if (!isUpdate && request.getExpiredAt().isBefore(now)) {
            throw new RuntimeException("Voucher mới tạo thì Ngày hết hạn phải ở tương lai!");
        }

        // 2. Logic phần trăm giảm giá
        if ("PERCENTAGE".equals(request.getDiscountType())) {
            if (request.getValue().doubleValue() > 100) {
                throw new RuntimeException("Giảm theo % không được vượt quá 100%!");
            }
            if (request.getMaxDiscountAmount() == null || request.getMaxDiscountAmount().doubleValue() <= 0) {
                throw new RuntimeException("Phải quy định số tiền giảm TỐI ĐA khi chọn giảm theo % !");
            }
        }

        voucher.setCode(request.getCode());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setValue(request.getValue());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setMinOrderValue(request.getMinOrderValue());

        if("FIXED_AMOUNT".equals(request.getDiscountType())) {
            voucher.setMaxDiscountAmount(request.getValue());
        } else {
            voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        }

        voucher.setStartDate(request.getStartDate());
        voucher.setExpiredAt(request.getExpiredAt());
    }

    public Voucher createVoucher(VoucherRequestDTO request) {
        Voucher voucher = new Voucher();
        mapDtoToEntity(voucher, request, false);
        return voucherRepository.save(voucher);
    }

    public Voucher updateVoucher(Long id, VoucherRequestDTO request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Voucher!"));
        mapDtoToEntity(voucher, request, true);
        return voucherRepository.save(voucher);
    }

    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }
}