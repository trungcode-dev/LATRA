package trungdevcode.latra.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.CheckoutRequestDTO;
import trungdevcode.latra.Entity.Order;
import trungdevcode.latra.Service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    // ==========================================
    // API MỚI CHO LUỒNG BÁN HÀNG POS (QUÉT SÚNG) - ĐANG CHẠY TỐT
    // ==========================================
    @PostMapping("/checkout-pos")
    public ResponseEntity<?> checkoutPOS(@RequestBody CheckoutRequestDTO request) {
        try {
            String result = orderService.checkoutPOS(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // ==========================================
    // CÁC API CŨ TẠM THỜI ĐÓNG BĂNG ĐỂ TRÁNH LỖI ĐỎ
    // (Sau này khôi phục hàm trong Service thì mở lại sau)
    // ==========================================
    /*
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<?> checkout(@PathVariable Long userId) {
        try {
            Order order = orderService.checkout(userId);
            return ResponseEntity.ok("Đặt hàng thành công! Mã đơn hàng: " + order.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false) String status,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrders(status, pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(orderService.getOrderDetails(orderId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long orderId,
            @RequestBody trungdevcode.latra.Dto.DonHangDTO.CapNhatTrangThai request) {
        try {
            orderService.updateOrderStatus(orderId, request);
            return ResponseEntity.ok("Cập nhật trạng thái thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    */
}