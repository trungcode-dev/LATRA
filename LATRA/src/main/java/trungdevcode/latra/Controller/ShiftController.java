package trungdevcode.latra.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.ShiftSummaryDTO;
import trungdevcode.latra.Repository.OrderRepository;
import trungdevcode.latra.Repository.UserRepository;
import trungdevcode.latra.Entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/shifts")
@CrossOrigin(origins = "http://localhost:5173")
public class ShiftController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/summary")
    public ResponseEntity<ShiftSummaryDTO> getShiftSummary(@RequestParam Long userId) {
        ShiftSummaryDTO summary = new ShiftSummaryDTO();

        // 1. Lấy thông tin nhân viên
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            summary.setStaffName(userOpt.get().getFullName() + " (" + userOpt.get().getUsername() + ")");
        } else {
            summary.setStaffName("Unknown Staff");
        }

        // 2. Format giờ mở ca (Giả lập mốc 08:00 sáng nay)
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        summary.setStartTime("08:00 - " + today);

        // 3. Gọi Database lấy số liệu THẬT
        int totalOrders = orderRepository.countOrdersTodayByUserId(userId);
        BigDecimal totalRevenue = orderRepository.sumRevenueTodayByUserId(userId);

        summary.setTotalOrders(totalOrders);
        summary.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        return ResponseEntity.ok(summary);
    }
}