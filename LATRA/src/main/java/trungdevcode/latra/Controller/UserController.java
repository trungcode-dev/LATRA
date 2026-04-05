package trungdevcode.latra.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.OrderHistoryDTO;
import trungdevcode.latra.Dto.UserDTO;
import trungdevcode.latra.Service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin("*") // Cho phép Frontend (Vue 3) gọi API không bị lỗi CORS
public class UserController {

    @Autowired
    private UserService userService;

    // API 1: Lấy danh sách User (Có thể lọc theo ROLE_ADMIN, ROLE_USER...)
    // VD gọi API: GET http://localhost:8080/api/admin/users?role=ADMIN
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(required = false) String role) {
        return ResponseEntity.ok(userService.getAllUsers(role));
    }

    // API 2: Cập nhật trạng thái (Khóa / Mở khóa)
    // VD gọi API: PUT http://localhost:8080/api/admin/users/1/status?status=0
    @PutMapping("/{id}/status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.toggleUserStatus(id, status);
        return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công!"));
    }

    // API 3: Reset mật khẩu
    // VD gọi API: PUT http://localhost:8080/api/admin/users/1/reset-password
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return ResponseEntity.ok(Map.of("message", "Đã reset mật khẩu về mặc định (123456)!"));
    }

    // API 4: Xem lịch sử mua hàng của 1 user
    // VD gọi API: GET http://localhost:8080/api/admin/users/1/orders
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderHistoryDTO>> getUserOrders(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserOrderHistory(id));
    }
}
