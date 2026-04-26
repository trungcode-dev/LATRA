package trungdevcode.latra.Service;
import trungdevcode.latra.Entity.User;
import trungdevcode.latra.Entity.Imei;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Dto.DonHangDTO;
import trungdevcode.latra.Entity.Cart;
import trungdevcode.latra.Entity.CartItem;
import trungdevcode.latra.Entity.OrderDetail;
import trungdevcode.latra.Entity.Order;
import trungdevcode.latra.Entity.OrderDetailKey;
import trungdevcode.latra.Entity.ProductVariant;
import trungdevcode.latra.Repository.CartRepository;
import trungdevcode.latra.Repository.ImeiRepository;
import trungdevcode.latra.Repository.OrderDetailRepository;
import trungdevcode.latra.Repository.OrderRepository;
import trungdevcode.latra.Repository.ProductVariantRepository;
import trungdevcode.latra.Repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ImeiRepository imeiRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CartRepository cartRepository;
    public Page<DonHangDTO.DanhSach> getOrders(String status, Pageable pageable) {
        Page<Order> orders = (status != null && !status.isEmpty())
                ? orderRepository.findByStatus(status, pageable)
                : orderRepository.findAll(pageable);

        return orders.map(order -> {
            DonHangDTO.DanhSach dto = modelMapper.map(order, DonHangDTO.DanhSach.class);
            dto.setCustomerName(order.getUser().getFullName());
            return dto;
        });
    }

    public DonHangDTO.ChiTiet getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
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
        Order order = orderRepository.findById(orderId)
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
    @Transactional
    public Order checkout(Long userId) {
        // 1. Lấy giỏ hàng
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng trống!"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Không có sản phẩm để thanh toán!");
        }

        // 2. TÌM USER ĐỐI TƯỢNG (Để gán vào OrderEntity)
        // Sửa lỗi: order.setUser(cart.getUserId()) -> Đỏ vì sai kiểu dữ liệu
        var user = userRepository.findById(cart.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        Order order = new Order();
        order.setUser(user); // Bây giờ gán Object User vào Object User là chuẩn
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Đổi từ List cũ sang List mới để tránh lỗi NullPointerException
        order.setOrderDetails(new java.util.ArrayList<>());

        double totalAmountAccumulator = 0; // Biến tạm để tính tổng

        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getVariant();

            if (variant.getStock() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + variant.getSku() + " đã hết hàng!");
            }

            variant.setStock(variant.getStock() - item.getQuantity());
            variantRepository.save(variant);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setVariant(variant);
            detail.setQuantity(item.getQuantity());
            // Giả sử variant.getPrice() trả về BigDecimal hoặc Double
            detail.setPrice(variant.getPrice());

            order.getOrderDetails().add(detail);

            // Tính toán tổng tiền (ép kiểu về double để tính cho dễ)
            totalAmountAccumulator += variant.getPrice().doubleValue() * item.getQuantity();
        }

        // 3. GÁN TỔNG TIỀN (Sửa lỗi gán double cho BigDecimal)
        order.setTotalAmount(BigDecimal.valueOf(totalAmountAccumulator));

        // 4. Xóa sạch giỏ hàng
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderRepository.save(order);
    }
    // ==========================================
    // API CHỐT ĐƠN CHO MÀN HÌNH POS (ĐỪNG XÓA NỮA NHÉ ^^)
    // ==========================================
    @Transactional
    public String checkoutPOS(trungdevcode.latra.Dto.CheckoutRequestDTO request) {
        if (request.getScannedImeis() == null || request.getScannedImeis().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống! Chưa quét mã IMEI nào.");
        }

        // Tạo Hóa Đơn mới
        Order order = new Order();
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId()).orElse(null);
            order.setUser(user);
        }
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus("COMPLETED");
        order.setCreatedAt(java.time.LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        java.util.Map<ProductVariant, Integer> variantCountMap = new java.util.HashMap<>();
        java.util.List<Imei> imeisToUpdate = new java.util.ArrayList<>();

        // Quét và đổi trạng thái IMEI
        for (String code : request.getScannedImeis()) {
            Imei imei = imeiRepository.findByImeiCode(code)
                    .orElseThrow(() -> new RuntimeException("Mã IMEI " + code + " không tồn tại!"));

            if (!"AVAILABLE".equals(imei.getStatus())) {
                throw new RuntimeException("Mã IMEI " + code + " đã bị bán hoặc không khả dụng!");
            }

            imei.setStatus("SOLD");
            imei.setOrder(savedOrder);
            imeisToUpdate.add(imei);

            ProductVariant variant = imei.getVariant();
            variantCountMap.put(variant, variantCountMap.getOrDefault(variant, 0) + 1);
        }
        imeiRepository.saveAll(imeisToUpdate);

        // Tạo Chi tiết Hóa đơn & Trừ Tồn kho
        java.util.List<OrderDetail> orderDetails = new java.util.ArrayList<>();
        for (java.util.Map.Entry<ProductVariant, Integer> entry : variantCountMap.entrySet()) {
            ProductVariant variant = entry.getKey();
            int quantity = entry.getValue();

            OrderDetail detail = new OrderDetail();
            detail.setId(new OrderDetailKey(savedOrder.getId(), variant.getId()));
            detail.setOrder(savedOrder);
            detail.setVariant(variant);
            detail.setQuantity(quantity);
            detail.setPrice(variant.getPrice());

            orderDetails.add(detail);

            long availableStock = imeiRepository.countByVariantAndStatus(variant, "AVAILABLE");
            variant.setStock((int) availableStock);
            variantRepository.save(variant);
        }
        orderDetailRepository.saveAll(orderDetails);

        return "Chốt đơn thành công! Mã Hóa Đơn: #" + savedOrder.getId();
    }
}
