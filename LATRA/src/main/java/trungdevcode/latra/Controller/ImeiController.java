package trungdevcode.latra.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.ImeiImportRequestDTO;
import trungdevcode.latra.Service.ImeiService;

@RestController
@RequestMapping("/api/v1/imeis")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ImeiController {

    private final ImeiService imeiService;

    // Lấy danh sách tất cả IMEI để hiển thị ở Tab "Tồn Kho IMEI"
    // API Lấy danh sách (Đã gắn Radar dò lỗi)
    @GetMapping
    public ResponseEntity<?> getAllImeis() {
        try {
            return ResponseEntity.ok(imeiService.findAllImeis());
        } catch (Exception e) {
            // In lỗi ra bảng đen của IntelliJ
            e.printStackTrace();
            // Gói cái lỗi thật sự ném thẳng về cho Vue.js
            String rootCause = e.getCause() != null ? e.getCause().getMessage() : "Không rõ";
            return ResponseEntity.status(500).body("LỖI BỊ ẨN TRONG JAVA: " + e.getMessage() + " | NGUYÊN NHÂN SÂU XA: " + rootCause);
        }
    }

    // Nhập kho IMEI (Xử lý khi bấm nút "Xác nhận nhập kho" trên Vue)
    @PostMapping("/import")
    public ResponseEntity<?> importImeis(@RequestBody ImeiImportRequestDTO request) {
        // Dùng đúng DTO chứa variantId và List<String> imeis
        imeiService.addImeisToVariant(request.getVariantId(), request.getImeis());
        return ResponseEntity.ok("Nhập kho thành công " + request.getImeis().size() + " thiết bị!");
    }

    // Xóa mã IMEI (Xử lý khi bấm nút Thùng rác ở Tab 2)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImei(@PathVariable Long id) {
        imeiService.deleteImei(id);
        return ResponseEntity.ok("Đã xóa mã IMEI khỏi hệ thống!");
    }
}