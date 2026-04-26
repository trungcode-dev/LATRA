package trungdevcode.latra.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    // 1. API Tạo đơn hàng (Hứng request từ phần 4 Postman)
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Object orderRequest) {
        // TODO: Chỗ này sau này ông gọi OrderService để lưu vào DB
        System.out.println("Dữ liệu đơn hàng gửi lên: " + orderRequest.toString());

        return ResponseEntity.ok("Giao tiếp thành công! Đã nhận được request tạo đơn hàng.");
    }

    // 2. API Áp dụng Voucher (Hứng request từ phần 5 Postman)
    @PostMapping("/apply-voucher")
    public ResponseEntity<?> applyVoucher(@RequestBody Object voucherRequest) {
        // TODO: Chỗ này sau này ông gọi logic check điều kiện Voucher
        System.out.println("Dữ liệu voucher gửi lên: " + voucherRequest.toString());

        return ResponseEntity.ok("Giao tiếp thành công! Đã nhận được request áp dụng mã giảm giá.");
    }
}
