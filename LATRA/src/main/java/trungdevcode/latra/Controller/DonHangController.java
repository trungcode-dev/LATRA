package trungdevcode.latra.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.DonHangDTO;
import trungdevcode.latra.Service.OrderService;

@RestController
@RequestMapping("/api/admin/orders") // Vẫn giữ endpoint chuẩn RESTful
@RequiredArgsConstructor
public class DonHangController {

    private final OrderService orderService;

    // API 1: Lấy danh sách đơn hàng
    // Ví dụ: GET /api/admin/orders?status=PENDING&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<DonHangDTO.DanhSach>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Mặc định sắp xếp đơn hàng mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getOrders(status, pageable));
    }

    // API 2: Xem chi tiết 1 đơn hàng
    // Ví dụ: GET /api/admin/orders/1
    @GetMapping("/{id}")
    public ResponseEntity<DonHangDTO.ChiTiet> getOrderDetails(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetails(id));
    }

    // API 3: Cập nhật trạng thái đơn (Duyệt / Giao / Hủy)
    // Ví dụ: PUT /api/admin/orders/1/status
    // Body JSON: { "status": "CANCELLED" }
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody DonHangDTO.CapNhatTrangThai request) {

        orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công!");
    }
}