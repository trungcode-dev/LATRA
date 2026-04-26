package trungdevcode.latra.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warranties")
@CrossOrigin("*")
public class WarrantyController {
    // 1. API Kích hoạt bảo hành (Hứng request POST từ phần 6 Postman)
    @PostMapping("/activate")
    public ResponseEntity<?> activateWarranty(@RequestBody Object warrantyRequest) {
        // TODO: Chỗ này tuần sau code logic Update DB trạng thái ACTIVE và cộng điểm VIP
        System.out.println("Dữ liệu kích hoạt bảo hành nhận được: " + warrantyRequest.toString());

        return ResponseEntity.ok("Giao tiếp thành công! Đã nhận lệnh kích hoạt bảo hành.");
    }

    // 2. API Tra cứu bảo hành (Hứng request GET từ phần 7 Postman)
    @GetMapping("/check")
    public ResponseEntity<?> checkWarranty(@RequestParam String imei) {
        // TODO: Chỗ này tuần sau code logic query DB tìm IMEI trả về ngày hết hạn
        System.out.println("Yêu cầu tra cứu bảo hành cho mã IMEI: " + imei);

        return ResponseEntity.ok("Giao tiếp thành công! Đang tra cứu IMEI: " + imei);
    }
}
