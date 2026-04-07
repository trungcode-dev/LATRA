package trungdevcode.latra.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungdevcode.latra.Dto.VoucherRequestDTO;
import trungdevcode.latra.Entity.Voucher;
import trungdevcode.latra.Repository.VoucherRepository;


import java.util.List;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    // 1. Lấy danh sách Voucher
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    // 2. Thêm mới Voucher
    public Voucher createVoucher(VoucherRequestDTO request) {
        Voucher voucher = new Voucher();
        voucher.setCode(request.getCode());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setValue(request.getValue());
        voucher.setExpiredAt(request.getExpiredAt());
        return voucherRepository.save(voucher);
    }

    // 3. Sửa Voucher
    public Voucher updateVoucher(Long id, VoucherRequestDTO request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Voucher này!"));

        voucher.setCode(request.getCode());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setValue(request.getValue());
        voucher.setExpiredAt(request.getExpiredAt());
        return voucherRepository.save(voucher);
    }

    // 4. Xóa hẳn Voucher (Bảng này không có status nên mình dùng xóa cứng luôn)
    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }
}
