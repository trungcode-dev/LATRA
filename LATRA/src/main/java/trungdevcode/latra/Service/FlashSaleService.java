package trungdevcode.latra.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Dto.FlashSaleRequestDTO;
import trungdevcode.latra.Entity.FlashSale;
import trungdevcode.latra.Entity.FlashSaleItem;
import trungdevcode.latra.Entity.ProductVariant;
import trungdevcode.latra.Repository.FlashSaleItemRepository;
import trungdevcode.latra.Repository.FlashSaleRepository;
import trungdevcode.latra.Repository.ProductVariantRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FlashSaleService {
    @Autowired
    private FlashSaleItemRepository flashSaleItemRepository;

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    // 1. HÀM TỰ ĐỘNG TÍNH TOÁN TRẠNG THÁI THEO THỜI GIAN THỰC
    private String calculateStatus(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) return "UPCOMING"; // Sắp diễn ra
        if (now.isAfter(endTime)) return "ENDED";       // Đã đóng
        return "ACTIVE";                                // Đang diễn ra
    }

    // 2. KIỂM TRA NGHIỆP VỤ (VALIDATION)
    private void validateFlashSaleLogic(FlashSaleRequestDTO request) {
        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().isEqual(request.getStartTime())) {
            throw new RuntimeException("Lỗi: Thời gian kết thúc phải lớn hơn thời gian bắt đầu!");
        }

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            Set<Long> variantIds = new HashSet<>();
            for (FlashSaleRequestDTO.ItemDTO item : request.getItems()) {
                if (!variantIds.add(item.getVariantId())) {
                    throw new RuntimeException("Lỗi: Sản phẩm ID " + item.getVariantId() + " bị trùng trong danh sách!");
                }
            }
        }
    }

    @Transactional
    public FlashSale createFlashSale(FlashSaleRequestDTO request) {
        validateFlashSaleLogic(request);

        FlashSale flashSale = new FlashSale();
        flashSale.setName(request.getName());
        flashSale.setStartTime(request.getStartTime());
        flashSale.setEndTime(request.getEndTime());
        flashSale.setStatus(calculateStatus(request.getStartTime(), request.getEndTime()));

        if (request.getItems() != null) {
            List<FlashSaleItem> items = request.getItems().stream().map(dto -> {
                ProductVariant variant = productVariantRepository.findById(dto.getVariantId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

                // Check trùng khung giờ với các đợt Sale khác
                long overlapCount = flashSaleItemRepository.countOverlappingSales(
                        variant.getId(), request.getStartTime(), request.getEndTime(), -1L);
                if (overlapCount > 0) {
                    throw new RuntimeException("Sản phẩm ID " + variant.getId() + " đã tham gia đợt Sale khác cùng giờ!");
                }

                FlashSaleItem item = new FlashSaleItem();
                item.setFlashSale(flashSale);
                item.setProductVariant(variant);
                item.setSalePrice(dto.getSalePrice());
                item.setQuantityLimit(dto.getQuantityLimit());
                return item;
            }).collect(Collectors.toList());
            flashSale.setItems(items);
        }
        return flashSaleRepository.save(flashSale);
    }

    public List<FlashSale> getAllFlashSales() {
        List<FlashSale> list = flashSaleRepository.findAll();
        // Cập nhật trạng thái tự động mỗi khi load danh sách cho chuẩn giờ thực
        list.forEach(fs -> {
            String newStatus = calculateStatus(fs.getStartTime(), fs.getEndTime());
            if (!newStatus.equals(fs.getStatus())) {
                fs.setStatus(newStatus);
                flashSaleRepository.save(fs);
            }
        });
        return list;
    }

    @Transactional
    public FlashSale updateFlashSale(Long id, FlashSaleRequestDTO request) {
        validateFlashSaleLogic(request);

        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt Flash Sale!"));

        flashSale.setName(request.getName());
        flashSale.setStartTime(request.getStartTime());
        flashSale.setEndTime(request.getEndTime());
        flashSale.setStatus(calculateStatus(request.getStartTime(), request.getEndTime()));

        // --- CHIÊU TRỊ LỖI HIBERNATE SESSION (FIX 500) ---
        // Lưu lại các item cũ vào Map để dùng lại Object, tránh xung đột Identifier
        Map<Long, FlashSaleItem> existingItemsMap = flashSale.getItems().stream()
                .collect(Collectors.toMap(item -> item.getProductVariant().getId(), item -> item));

        // Xóa danh sách cũ trong Collection (Hibernate sẽ lo việc mồ côi object)
        flashSale.getItems().clear();

        if (request.getItems() != null) {
            List<FlashSaleItem> updatedItems = request.getItems().stream().map(dto -> {
                ProductVariant variant = productVariantRepository.findById(dto.getVariantId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

                // Check trùng khung giờ (trừ chính nó)
                long overlapCount = flashSaleItemRepository.countOverlappingSales(
                        variant.getId(), request.getStartTime(), request.getEndTime(), flashSale.getId());
                if (overlapCount > 0) {
                    throw new RuntimeException("Sản phẩm ID " + variant.getId() + " bị trùng khung giờ Sale khác!");
                }

                // NẾU ĐÃ CÓ TRONG DB THÌ DÙNG LẠI, CHƯA CÓ THÌ MỚI NEW
                FlashSaleItem item = existingItemsMap.getOrDefault(variant.getId(), new FlashSaleItem());
                item.setFlashSale(flashSale);
                item.setProductVariant(variant);
                item.setSalePrice(dto.getSalePrice());
                item.setQuantityLimit(dto.getQuantityLimit());
                return item;
            }).collect(Collectors.toList());

            flashSale.getItems().addAll(updatedItems);
        }

        return flashSaleRepository.save(flashSale);
    }

    @Transactional
    public void deleteFlashSale(Long id) {
        flashSaleRepository.deleteById(id);
    }
}