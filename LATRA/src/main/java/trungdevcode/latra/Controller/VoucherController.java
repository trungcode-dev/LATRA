package trungdevcode.latra.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.VoucherRequestDTO;
import trungdevcode.latra.Entity.Voucher;
import trungdevcode.latra.Service.VoucherService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers/admin")
@CrossOrigin(origins = "http://localhost:5173") // 🔥 Nhớ cái này để Vue gọi không bị lỗi CORS
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping
    public ResponseEntity<List<Voucher>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.getAllVouchers());
    }

    @PostMapping
    public ResponseEntity<Voucher> createVoucher(@Valid @RequestBody VoucherRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voucherService.createVoucher(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voucher> updateVoucher(@PathVariable Long id, @Valid @RequestBody VoucherRequestDTO request) {
        return ResponseEntity.ok(voucherService.updateVoucher(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok("Đã xóa mã Voucher thành công!");
    }

    // 🔥 ĐOẠN NÀY LÀ MỚI THÊM: Cổng API cho nút "Áp dụng" bên POS
    @GetMapping("/check")
    public ResponseEntity<?> checkVoucher(
            @RequestParam String code,
            @RequestParam BigDecimal total) {
        try {
            return ResponseEntity.ok(voucherService.checkVoucher(code, total));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }
}