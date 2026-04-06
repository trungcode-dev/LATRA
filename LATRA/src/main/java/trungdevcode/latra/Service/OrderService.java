package trungdevcode.latra.Service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Dto.DonHangDTO;
import trungdevcode.latra.Entity.OrderEntity;
import trungdevcode.latra.Repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    // Gọi theo cấu trúc mới: OrderRepository.Tên_Bảng
    private final OrderRepository.Order orderRepo;
    private final OrderRepository.ProductVariant variantRepo;
    private final ModelMapper modelMapper;

    // 1. API Danh sách
    public Page<DonHangDTO.DanhSach> getOrders(String status, Pageable pageable) {
        Page<OrderEntity> orders = (status != null && !status.isEmpty())
                ? orderRepo.findByStatus(status, pageable)
                : orderRepo.findAll(pageable);

        return orders.map(order -> {
            DonHangDTO.DanhSach dto = modelMapper.map(order, DonHangDTO.DanhSach.class);
            dto.setCustomerName(order.getUser().getFullName());
            return dto;
        });
    }

    // 2. API Chi tiết
    public DonHangDTO.ChiTiet getOrderDetails(Long orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        DonHangDTO.ChiTiet dto = modelMapper.map(order, DonHangDTO.ChiTiet.class);
        dto.setCustomerName(order.getUser().getFullName());
        dto.setCustomerPhone(order.getUser().getPhone());

        if (order.getShipment() != null) {
            dto.setShippingAddress(order.getShipment().getAddress());
        }

        List<DonHangDTO.Item> items = order.getOrderDetails().stream().map(detail -> {
            DonHangDTO.Item itemDto = new DonHangDTO.Item();
            itemDto.setVariantId(detail.getVariant().getId());
            itemDto.setProductName(detail.getVariant().getProduct().getName());
            itemDto.setColor(detail.getVariant().getColor());
            itemDto.setStorage(detail.getVariant().getStorage());
            itemDto.setQuantity(detail.getQuantity());
            itemDto.setPrice(detail.getPrice());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }

    // 3. API Cập nhật & Tự động hoàn tồn kho khi Hủy
    @Transactional
    public void updateOrderStatus(Long orderId, DonHangDTO.CapNhatTrangThai request) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        String oldStatus = order.getStatus();
        String newStatus = request.getStatus().toUpperCase();

        if ("CANCELLED".equals(newStatus) && !"CANCELLED".equals(oldStatus)) {
            for (OrderEntity.OrderDetail detail : order.getOrderDetails()) {
                OrderEntity.ProductVariant variant = detail.getVariant();
                variant.setStock(variant.getStock() + detail.getQuantity());
                // Lưu cập nhật tồn kho
                variantRepo.save(variant);
            }
        }

        order.setStatus(newStatus);
        orderRepo.save(order);
    }
}
