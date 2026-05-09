package trungdevcode.latra.Controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Service.ThanhToanServices;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class ThanhToanController {

    private final ThanhToanServices thanhToanService;

    // API 1: Lấy URL để chuyển hướng khách hàng sang trang VNPAY
    @GetMapping("/vnpay/create-url/{orderId}")
    public ResponseEntity<String> createPaymentUrl(@PathVariable Long orderId, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String paymentUrl = thanhToanService.createVNPayUrl(orderId, ipAddress);
        return ResponseEntity.ok(paymentUrl);
    }

    // API 2: VNPAY gọi về sau khi khách thanh toán xong (Return URL)
    @GetMapping("/vnpay_return")
    public ResponseEntity<String> vnpayReturn(HttpServletRequest request) {
        Map<String, String> queryParams = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            queryParams.put(paramName, paramValue);
        }

        String result = thanhToanService.processVnPayReturn(queryParams);

        // Trong thực tế, chỗ này thường return về một file HTML hoặc redirect về trang Frontend (React/Vue)
        return ResponseEntity.ok(result);
    }
}
