package trungdevcode.latra.Controller;

import trungdevcode.latra.Service.WarrantyLoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class LoyaltyWarrantyController {

    @Autowired
    private WarrantyLoyaltyService warrantyLoyaltyService;

    // ==========================================
    // API DÀNH CHO KHÁCH VÃNG LAI (PUBLIC)
    // ==========================================

    // API Tra cứu bảo hành (Nhập IMEI hoặc SĐT)
    // VD: GET /api/public/warranties/lookup?keyword=0901234567
    @GetMapping("/public/warranties/lookup")
    public ResponseEntity<?> lookupWarranty(@RequestParam String keyword) {
        return ResponseEntity.ok(warrantyLoyaltyService.lookupWarranty(keyword));
    }

    // ==========================================
    // API NỘI BỘ (DÀNH CHO HỆ THỐNG / ADMIN)
    // ==========================================

    // API này thực chất Thằng M1 (Đan) sẽ gọi bên trong hàm UpdateOrder của nó.
    // Tui tạo API này ra đây để ông tự Test trên Postman cho dễ.
    // VD: POST /api/admin/orders/1/trigger-post-sale
    @PostMapping("/admin/orders/{orderId}/trigger-post-sale")
    public ResponseEntity<?> triggerWarrantyAndLoyalty(@PathVariable Long orderId) {
        warrantyLoyaltyService.triggerPostSaleServices(orderId);
        return ResponseEntity.ok(Map.of("message", "Đã kích hoạt Bảo hành 12 tháng và cộng điểm VIP thành công!"));
    }
}
