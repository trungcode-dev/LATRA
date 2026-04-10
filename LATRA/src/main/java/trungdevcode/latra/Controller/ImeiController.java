package trungdevcode.latra.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Service.ImeiService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/imeis")
@RequiredArgsConstructor
public class ImeiController {

    private final ImeiService imeiService;

    @PostMapping("/import/{variantId}")
    public ResponseEntity<?> importImeis(
            @PathVariable Long variantId,
            @RequestBody List<String> imeiCodes) {
        try {
            imeiService.addImeisToVariant(variantId, imeiCodes);
            return ResponseEntity.ok("Nhập kho thành công " + imeiCodes.size() + " máy. Tồn kho đã tự động tăng!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi nhập kho: " + e.getMessage());
        }
    }

    @PostMapping("/export/{orderId}")
    public ResponseEntity<?> exportImeis(
            @PathVariable Long orderId,
            @RequestBody List<String> scannedImeis) {
        try {
            imeiService.exportImeisForOrder(orderId, scannedImeis);

            return ResponseEntity.ok("Xuất kho thành công! Đã gắn " + scannedImeis.size() + " mã IMEI vào đơn hàng số " + orderId + ". Tồn kho đã tự động giảm!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi xuất kho: " + e.getMessage());
        }
    }
    @GetMapping("/available/{variantId}")
    public ResponseEntity<?> getAvailableImeis(@PathVariable Long variantId) {
        try {
            List<String> availableImeis = imeiService.getAvailableImeis(variantId);
            return ResponseEntity.ok(availableImeis);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}