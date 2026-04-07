package trungdevcode.latra.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.UpdateProfileRequest;
import trungdevcode.latra.Dto.WarrantyResponseDTO;
import trungdevcode.latra.Entity.OrderEntity;
import trungdevcode.latra.Entity.User;
import trungdevcode.latra.Repository.OrderRepository;
import trungdevcode.latra.Repository.UserRepository;
import trungdevcode.latra.Repository.WarrantyRepository;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class CustomerProfileController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WarrantyRepository warrantyRepository;

    /**
     * API 1: Xem lịch sử đơn hàng của người đang đăng nhập
     * Yêu cầu Frontend phải truyền Token vào Header
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getOrderHistory(Principal principal) {
        // Lấy thông tin user đang đăng nhập từ Token
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // Lấy danh sách đơn hàng
        List<OrderEntity> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return ResponseEntity.ok(orders);
    }

    /**
     * API 2: Cập nhật thông tin cá nhân
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // Cập nhật các trường
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

        // Lưu vào DB
        userRepository.save(user);

        return ResponseEntity.ok("Cập nhật hồ sơ thành công!");
    }

    /**
     * API 3: Tra cứu bảo hành (Không cần đăng nhập)
     * Đường dẫn: GET /api/profile/warranty/check?phone=0987654321
     */
    @GetMapping("/warranty/check")
    public ResponseEntity<?> checkWarranty(@RequestParam String phone) {
        List<WarrantyResponseDTO> warranties = warrantyRepository.findWarrantyDetailsByPhone(phone);

        if (warranties.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thông tin bảo hành cho số điện thoại này.");
        }

        return ResponseEntity.ok(warranties);
    }
}
