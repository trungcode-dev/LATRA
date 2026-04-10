package trungdevcode.latra.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Entity.Imei;
import trungdevcode.latra.Entity.Order;
import trungdevcode.latra.Entity.ProductVariant;
import trungdevcode.latra.Repository.ImeiRepository;
import trungdevcode.latra.Repository.OrderRepository;
import trungdevcode.latra.Repository.ProductVariantRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImeiService {

    private final ImeiRepository imeiRepository;
    private final ProductVariantRepository variantRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public List<Imei> addImeisToVariant(Long variantId, List<String> imeiCodes) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Variant!"));

        List<Imei> newImeis = new ArrayList<>();
        for (String code : imeiCodes) {
            Imei imei = new Imei();
            imei.setImeiCode(code);
            imei.setVariant(variant);
            imei.setStatus("AVAILABLE");
            newImeis.add(imei);
        }

        List<Imei> savedImeis = imeiRepository.saveAll(newImeis);

        updateVariantStock(variant);

        return savedImeis;
    }

    private void updateVariantStock(ProductVariant variant) {
        long availableCount = imeiRepository.countByVariantAndStatus(variant, "AVAILABLE");
        variant.setStock((int) availableCount);
        variantRepository.save(variant);
    }

    @Transactional
    public void exportImeisForOrder(Long orderId, List<String> scannedImeis) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        for (String code : scannedImeis) {
            Imei imei = imeiRepository.findByImeiCode(code)
                    .orElseThrow(() -> new RuntimeException("Mã IMEI " + code + " không tồn tại trong kho!"));

            if (!"AVAILABLE".equals(imei.getStatus())) {
                throw new RuntimeException("Mã IMEI " + code + " đã được bán hoặc bị lỗi!");
            }

            imei.setStatus("SOLD");
            imei.setOrder(order);
            imeiRepository.save(imei);

            updateVariantStock(imei.getVariant());
        }
    }

    public List<String> getAvailableImeis(Long variantId) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Variant!"));

        List<Imei> availableImeis = imeiRepository.findByVariantAndStatus(variant, "AVAILABLE");

        List<String> imeiCodes = new ArrayList<>();
        for (Imei imei : availableImeis) {
            imeiCodes.add(imei.getImeiCode());
        }
        return imeiCodes;
    }
}