package trungdevcode.latra.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Config.VnpayConfig;
import trungdevcode.latra.Entity.OrderDetail;
import trungdevcode.latra.Entity.OrderEntity;
import trungdevcode.latra.Entity.Payment;
import trungdevcode.latra.Entity.ProductVariant;
import trungdevcode.latra.Repository.OrderRepository;
import trungdevcode.latra.Repository.PaymentRepository;
import trungdevcode.latra.Repository.ProductVariantRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ThanhToanServices {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductVariantRepository variantRepository;

    // 1. TẠO URL THANH TOÁN VNPAY
    public String createVNPayUrl(Long orderId, String ipAddress) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        long amount = order.getTotalAmount().longValue() * 100; // VNPAY yêu cầu nhân 100

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VnpayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(orderId));
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang LATRA " + orderId);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddress);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15); // Hết hạn sau 15 phút
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // Sắp xếp tham số để tạo chuỗi Hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        try {
            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append('&');
                    hashData.append('&');
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hóa VNPAY");
        }

        query.setLength(query.length() - 1);
        hashData.setLength(hashData.length() - 1);

        String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return VnpayConfig.vnp_Url + "?" + query.toString();
    }

    // 2. XỬ LÝ KẾT QUẢ TRẢ VỀ TỪ VNPAY VÀ LOGIC HOÀN KHO
    @Transactional // Đảm bảo tính toàn vẹn dữ liệu
    public String processVnPayReturn(Map<String, String> queryParams) {
        String vnp_SecureHash = queryParams.get("vnp_SecureHash");
        queryParams.remove("vnp_SecureHash");
        queryParams.remove("vnp_SecureHashType");

        // Xác thực chữ ký
        List<String> fieldNames = new ArrayList<>(queryParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();

        try {
            for (String fieldName : fieldNames) {
                String fieldValue = queryParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString())).append('&');
                }
            }
        } catch (Exception e) {
            return "LỖI XÁC THỰC";
        }
        hashData.setLength(hashData.length() - 1);
        String signValue = VnpayConfig.hmacSHA512(VnpayConfig.vnp_HashSecret, hashData.toString());

        if (signValue.equals(vnp_SecureHash)) {
            Long orderId = Long.parseLong(queryParams.get("vnp_TxnRef"));
            String responseCode = queryParams.get("vnp_ResponseCode");

            OrderEntity order = orderRepository.findById(orderId).orElseThrow();
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setMethod("VNPAY");
            payment.setPaidAt(LocalDateTime.now());

            if ("00".equals(responseCode)) {
                // THANH TOÁN THÀNH CÔNG
                order.setStatus("PAID"); // Đã thanh toán
                payment.setStatus("SUCCESS");

                orderRepository.save(order);
                paymentRepository.save(payment);
                return "THANH TOÁN THÀNH CÔNG";
            } else {
                // THANH TOÁN THẤT BẠI / KHÁCH HỦY GIAO DỊCH
                order.setStatus("CANCELLED");
                payment.setStatus("FAILED");

                // LOGIC CỐT LÕI: NHẢ KHO (REVERT STOCK)
                for (OrderDetail detail : order.getOrderDetails()) {
                    ProductVariant variant = detail.getVariant();
                    // Cộng lại tồn kho đã bị trừ lúc nhấn Checkout
                    variant.setStock(variant.getStock() + detail.getQuantity());
                    variantRepository.save(variant);
                }

                orderRepository.save(order);
                paymentRepository.save(payment);
                return "GIAO DỊCH BỊ HỦY - ĐÃ HOÀN LẠI TỒN KHO";
            }
        }
        return "CHỮ KÝ KHÔNG HỢP LỆ";
    }
}
