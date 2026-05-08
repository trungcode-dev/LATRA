package trungdevcode.latra.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungdevcode.latra.Dto.VoucherRequestDTO;
import trungdevcode.latra.Entity.Voucher;
import trungdevcode.latra.Repository.VoucherRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 🔥 ĐOẠN NÀY LÀ MỚI THÊM: Xử lý kiểm tra mã cho màn hình POS
    public Map<String, Object> checkVoucher(String code, BigDecimal orderTotal) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại!"));

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(voucher.getStartDate())) {
            throw new RuntimeException("Mã giảm giá này chưa tới thời gian áp dụng!");
        }
        if (now.isAfter(voucher.getExpiredAt())) {
            throw new RuntimeException("Mã giảm giá này đã hết hạn!");
        }
        if (voucher.getUsageLimit() <= 0) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng!");
        }
        if (orderTotal.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt mức tối thiểu " + voucher.getMinOrderValue() + "đ để dùng mã này!");
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if ("FIXED_AMOUNT".equals(voucher.getDiscountType())) {
            discountAmount = voucher.getValue();
        } else if ("PERCENTAGE".equals(voucher.getDiscountType())) {
            discountAmount = orderTotal.multiply(voucher.getValue()).divide(new BigDecimal("100"));
            if (voucher.getMaxDiscountAmount() != null && discountAmount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                discountAmount = voucher.getMaxDiscountAmount();
            }
        }

        if (discountAmount.compareTo(orderTotal) > 0) {
            discountAmount = orderTotal;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("discountAmount", discountAmount);
        response.put("voucherCode", voucher.getCode());
        return response;
    }
}