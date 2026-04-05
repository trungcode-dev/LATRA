package trungdevcode.latra.Service;


import org.springframework.beans.factory.annotation.Autowired;
// Tạm comment dòng mã hóa pass nếu M5 chưa làm Security
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import trungdevcode.latra.Dto.OrderHistoryDTO;
import trungdevcode.latra.Dto.UserDTO;
import trungdevcode.latra.Entity.Order;
import trungdevcode.latra.Entity.Role;
import trungdevcode.latra.Entity.User;
import trungdevcode.latra.Repository.OrderRepository;
import trungdevcode.latra.Repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    // 1. Lấy danh sách Khách hàng/Nhân viên
    public List<UserDTO> getAllUsers(String roleName) {
        List<User> users = userRepository.findAllByRoleName(roleName);
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 2. Khóa / Mở khóa tài khoản
    public void toggleUserStatus(Long userId, Integer status) {
        // VALIDATE: Chỉ chấp nhận 0 hoặc 1
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ! Chỉ chấp nhận 0 (Khóa) hoặc 1 (Mở khóa).");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Hệ thống không tìm thấy tài khoản với ID: " + userId));

        user.setStatus(status);
        userRepository.save(user);
    }

    // 3. Reset Password
    public void resetPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Hệ thống không tìm thấy tài khoản với ID: " + userId));

        user.setPassword("123456");
        userRepository.save(user);
    }

    // 4. Lấy lịch sử mua hàng
    public List<OrderHistoryDTO> getUserOrderHistory(Long userId) {
        // VALIDATE: Kiểm tra user có tồn tại không trước
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("Hệ thống không tìm thấy tài khoản với ID: " + userId);
        }

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(order -> {
            OrderHistoryDTO dto = new OrderHistoryDTO();
            dto.setOrderId(order.getId());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setStatus(order.getStatus());
            dto.setCreatedAt(order.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    // Hàm phụ: Chuyển Entity sang DTO
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return dto;
    }
}
