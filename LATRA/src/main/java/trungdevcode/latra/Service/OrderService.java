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
            dto.setCustomerName(order.getUser() != null ? order.getUser().getFullName() : "Khách lẻ");
            dto.setCustomerPhone(order.getUser() != null ? order.getUser().getPhone() : "");
            return dto;
        });
    }

    public DonHangDTO.ChiTiet getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        DonHangDTO.ChiTiet dto = modelMapper.map(order, DonHangDTO.ChiTiet.class);

        // Gán tên khách hàng
        dto.setCustomerName(order.getUser() != null ? order.getUser().getFullName() : "Khách lẻ");
        dto.setCustomerPhone(order.getUser() != null ? order.getUser().getPhone() : "");

        // Gán tên nhân viên thu ngân
        dto.setEmployeeName(order.getEmployee() != null ? order.getEmployee().getFullName() : "Nhân viên ẩn danh");

        dto.setCreatedAt(order.getCreatedAt());

        // Đẩy thông tin thanh toán chia nửa ra DTO để in Bill
        dto.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod() : "CASH");
        dto.setCashAmount(order.getCashAmount() != null ? order.getCashAmount() : BigDecimal.ZERO);
        dto.setTransferAmount(order.getTransferAmount() != null ? order.getTransferAmount() : BigDecimal.ZERO);

        // 👉 THÊM DÒNG NÀY: Trả ghi chú (chứa gói bảo hành) ra DTO để UI hiển thị
        dto.setNote(order.getNote());

        if (order.getShipment() != null) {
            dto.setShippingAddress(order.getShipment().getAddress());
        }

        List<Imei> imeisOfOrder = imeiRepository.findByOrder(order);

        List<DonHangDTO.Item> items = order.getOrderDetails().stream().map(detail -> {
            DonHangDTO.Item itemDto = new DonHangDTO.Item();
            itemDto.setVariantId(detail.getVariant().getId());
            itemDto.setProductName(detail.getVariant().getProduct() != null ? detail.getVariant().getProduct().getName() : "Sản phẩm không xác định");
            itemDto.setColor(detail.getVariant().getColor());
            itemDto.setStorage(detail.getVariant().getStorage());
            itemDto.setQuantity(detail.getQuantity());
            itemDto.setPrice(detail.getPrice());

            String imeiCodes = imeisOfOrder.stream()
                    .filter(imei -> imei.getVariant().getId().equals(detail.getVariant().getId()))
                    .map(Imei::getImeiCode)
                    .collect(Collectors.joining(", "));

            itemDto.setImeiCode(imeiCodes.isEmpty() ? "N/A" : imeiCodes);
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
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Giỏ hàng trống!"));
        if (cart.getItems().isEmpty()) throw new RuntimeException("Không có sản phẩm để thanh toán!");

        var user = userRepository.findById(cart.getUserId()).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderDetails(new java.util.ArrayList<>());

        double totalAmountAccumulator = 0;

        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getVariant();
            if (variant.getStock() < item.getQuantity()) throw new RuntimeException("Sản phẩm " + variant.getSku() + " đã hết hàng!");
            variant.setStock(variant.getStock() - item.getQuantity());
            variantRepository.save(variant);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setVariant(variant);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(variant.getPrice());

            order.getOrderDetails().add(detail);
            totalAmountAccumulator += variant.getPrice().doubleValue() * item.getQuantity();
        }

        order.setTotalAmount(BigDecimal.valueOf(totalAmountAccumulator));
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderRepository.save(order);
    }

    @Transactional
    public String checkoutPOS(trungdevcode.latra.Dto.CheckoutRequestDTO request) {
        if (request.getScannedImeis() == null || request.getScannedImeis().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống! Chưa quét mã IMEI nào.");
        }

        Order order = new Order();

        // 1. BẮT NHÂN VIÊN THU NGÂN
        if (request.getUserId() != null) {
            User employee = userRepository.findById(request.getUserId()).orElse(null);
            order.setEmployee(employee);
        }

        // 2. BẮT KHÁCH HÀNG
        if (request.getCustomerPhone() != null && !request.getCustomerPhone().trim().isEmpty()) {
            User customer = userRepository.findByPhone(request.getCustomerPhone());

            if (customer == null) {
                customer = new User();
                String name = (request.getCustomerName() != null && !request.getCustomerName().trim().isEmpty())
                        ? request.getCustomerName() : "Khách lẻ";
                customer.setFullName(name);
                customer.setPhone(request.getCustomerPhone());

                customer.setUsername(request.getCustomerPhone());
                customer.setPassword("123456");
                customer.setEmail(request.getCustomerPhone() + "@khach.com");
                customer.setAddress("Khách mua tại quầy");
                customer.setStatus(1);

                customer = userRepository.save(customer);
            }
            order.setUser(customer);
        }

        // 3. LOGIC LƯU SỐ TIỀN THANH TOÁN (TIỀN MẶT, CHUYỂN KHOẢN, KẾT HỢP)
        order.setTotalAmount(request.getTotalAmount());

        // 👉 ĐÃ THÊM LOGIC LƯU GHI CHÚ BẢO HÀNH TỪ VUE GỬI XUỐNG
        order.setNote(request.getNote());

        String method = request.getPaymentMethod();
        order.setPaymentMethod(method != null ? method : "CASH");

        if ("CASH".equals(method)) {
            order.setCashAmount(request.getTotalAmount());
            order.setTransferAmount(BigDecimal.ZERO);
        } else if ("TRANSFER".equals(method)) {
            order.setCashAmount(BigDecimal.ZERO);
            order.setTransferAmount(request.getTotalAmount());
        } else if ("SPLIT".equals(method)) {
            // Thanh toán kết hợp: Hứng đúng số tiền khách gõ ở Frontend
            order.setCashAmount(request.getCashAmount() != null ? request.getCashAmount() : BigDecimal.ZERO);
            order.setTransferAmount(request.getTransferAmount() != null ? request.getTransferAmount() : BigDecimal.ZERO);
        } else {
            order.setCashAmount(BigDecimal.ZERO);
            order.setTransferAmount(BigDecimal.ZERO);
        }

        order.setStatus("COMPLETED");
        order.setCreatedAt(java.time.LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Xử lý tồn kho và IMEI
        java.util.Map<ProductVariant, Integer> variantCountMap = new java.util.HashMap<>();
        java.util.List<Imei> imeisToUpdate = new java.util.ArrayList<>();

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