package trungdevcode.latra.Service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Dto.DonHangDTO;
import trungdevcode.latra.Entity.OrderDetail;
import trungdevcode.latra.Entity.OrderEntity;
import trungdevcode.latra.Entity.ProductVariant;
import trungdevcode.latra.Repository.OrderRepository;
import trungdevcode.latra.Repository.ProductVariantRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductVariantRepository variantRepository;
    private final ModelMapper modelMapper;

    public Page<DonHangDTO.DanhSach> getOrders(String status, Pageable pageable) {
        Page<OrderEntity> orders = (status != null && !status.isEmpty())
                ? orderRepository.findByStatus(status, pageable)
                : orderRepository.findAll(pageable);

        return orders.map(order -> {
            DonHangDTO.DanhSach dto = modelMapper.map(order, DonHangDTO.DanhSach.class);
            dto.setCustomerName(order.getUser().getFullName());
            return dto;
        });
    }

    public DonHangDTO.ChiTiet getOrderDetails(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
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
            if (detail.getVariant().getProduct() != null) {
                itemDto.setProductName(detail.getVariant().getProduct().getName());
            } else {
                itemDto.setProductName("Sản phẩm không xác định"); // Giá trị mặc định nếu rỗng
            }
            itemDto.setColor(detail.getVariant().getColor());
            itemDto.setStorage(detail.getVariant().getStorage());
            itemDto.setQuantity(detail.getQuantity());
            itemDto.setPrice(detail.getPrice());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, DonHangDTO.CapNhatTrangThai request) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        String oldStatus = order.getStatus();
        String newStatus = request.getStatus().toUpperCase();

        if ("CANCELLED".equals(newStatus) && !"CANCELLED".equals(oldStatus)) {
            for (OrderDetail detail : order.getOrderDetails()) {
                ProductVariant variant = detail.getVariant();
                variant.setStock(variant.getStock() + detail.getQuantity());
                variantRepository.save(variant);
            }
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}
