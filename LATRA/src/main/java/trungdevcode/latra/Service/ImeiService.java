package trungdevcode.latra.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Entity.Imei;
import trungdevcode.latra.Entity.ProductVariant;
import trungdevcode.latra.Repository.ImeiRepository;
import trungdevcode.latra.Repository.ProductVariantRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImeiService {

    private final ImeiRepository imeiRepository;
    private final ProductVariantRepository variantRepository;

    // Lấy toàn bộ danh sách để xem ở Tab 2 (ĐÃ SỬA LẠI THÀNH DẠNG MAP ĐỂ CHỐNG LỖI 500)
    // Lấy toàn bộ danh sách để xem ở Tab 2 (ĐÃ SỬA LẠI THÀNH DẠNG MAP ĐỂ CHỐNG LỖI 500)
    public List<Map<String, Object>> findAllImeis() {
        List<Imei> imeis = imeiRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Imei imei : imeis) {
            Map<String, Object> imeiMap = new HashMap<>();
            imeiMap.put("id", imei.getId());
            imeiMap.put("imeiCode", imei.getImeiCode());
            imeiMap.put("status", imei.getStatus());
            imeiMap.put("createdAt", imei.getCreatedAt());

            if (imei.getVariant() != null) {
                Map<String, Object> variantMap = new HashMap<>();
                variantMap.put("color", imei.getVariant().getColor());
                variantMap.put("storage", imei.getVariant().getStorage());

                // 🔥 ĐÂY RỒI! BƠM GIÁ TIỀN VÀO ĐÂY ÔNG ƠI!
                variantMap.put("price", imei.getVariant().getPrice());

                // (Tùy chọn) Nếu Entity Variant của ông có lưu link ảnh thì bơm luôn vào,
                // để màn hình POS nó không bị hiện "No Image"
                // variantMap.put("image", imei.getVariant().getImage());

                if (imei.getVariant().getProduct() != null) {
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("name", imei.getVariant().getProduct().getName());

                    // (Tùy chọn) Nếu ảnh lưu ở Product cha thì nhét vào đây
                    // productMap.put("image", imei.getVariant().getProduct().getImage());

                    variantMap.put("product", productMap);
                }
                imeiMap.put("variant", variantMap);
            }
            result.add(imeiMap);
        }
        return result;
    }

    // LOGIC NHẬP KHO: Thêm IMEI và Tự động cộng tồn kho
    @Transactional
    public void addImeisToVariant(Long variantId, List<String> imeiCodes) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản sản phẩm!"));

        List<Imei> newImeis = new ArrayList<>();
        for (String code : imeiCodes) {
            // Kiểm tra trùng mã IMEI
            if (imeiRepository.existsByImeiCode(code)) {
                throw new RuntimeException("Mã IMEI " + code + " đã tồn tại trong hệ thống!");
            }

            Imei imei = new Imei();
            imei.setImeiCode(code);
            imei.setVariant(variant);
            imei.setStatus("AVAILABLE");
            newImeis.add(imei);
        }

        // Lưu danh sách IMEI vào DB
        imeiRepository.saveAll(newImeis);

        // Cập nhật lại cột stock
        updateVariantStock(variant);
    }

    // LOGIC XÓA MÃ: Xóa và Tự động trừ tồn kho
    @Transactional
    public void deleteImei(Long id) {
        Imei imei = imeiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mã IMEI không tồn tại!"));

        ProductVariant variant = imei.getVariant();

        // Thực hiện xóa
        imeiRepository.delete(imei);

        // Cập nhật lại tồn kho sau khi xóa
        updateVariantStock(variant);
    }

    // HÀM DÙNG CHUNG: Đếm số lượng thực tế trong kho và ghi đè vào cột stock
    private void updateVariantStock(ProductVariant variant) {
        long availableCount = imeiRepository.countByVariantAndStatus(variant, "AVAILABLE");
        variant.setStock((int) availableCount);
        variantRepository.save(variant);
    }
}