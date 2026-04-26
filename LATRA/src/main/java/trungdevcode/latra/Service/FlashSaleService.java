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

import java.util.HashSet;
import java.util.List;
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

    // --- HÀM DÙNG CHUNG ĐỂ KIỂM TRA NGHIỆP VỤ ---
    private void validateFlashSaleLogic(FlashSaleRequestDTO request) {
        // 1. Kiểm tra Thời gian kết thúc phải SAU thời gian bắt đầu
        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().isEqual(request.getStartTime())) {
            throw new RuntimeException("Lỗi: Thời gian kết thúc phải lớn hơn thời gian bắt đầu!");
        }

        // 2. Kiểm tra Trùng lặp sản phẩm trong CÙNG 1 danh sách gửi lên
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            Set<Long> variantIds = new HashSet<>();
            for (FlashSaleRequestDTO.ItemDTO item : request.getItems()) {
                if (!variantIds.add(item.getVariantId())) {
                    throw new RuntimeException("Lỗi: Phát hiện sản phẩm trùng lặp trong danh sách (Phiên bản ID: " + item.getVariantId() + "). Vui lòng gộp lại!");
                }
            }
        }
    }

    @Transactional
    public FlashSale createFlashSale(FlashSaleRequestDTO request) {
        // GỌI HÀM KIỂM TRA NGHIỆP VỤ
        validateFlashSaleLogic(request);

        FlashSale flashSale = new FlashSale();
        flashSale.setName(request.getName());
        flashSale.setStartTime(request.getStartTime());
        flashSale.setEndTime(request.getEndTime());
        flashSale.setStatus(true);

        if (request.getItems() != null) {
            List<FlashSaleItem> items = request.getItems().stream().map(dto -> {
                ProductVariant variant = productVariantRepository.findById(dto.getVariantId())
                        .orElseThrow(() -> new RuntimeException("Phiên bản SP ID " + dto.getVariantId() + " không tồn tại!"));

                // Kiểm tra giá Sale
                if (dto.getSalePrice().compareTo(variant.getPrice()) >= 0) {
                    throw new RuntimeException("Lỗi: Giá Sale (" + dto.getSalePrice() + "đ) phải nhỏ hơn Giá Gốc (" + variant.getPrice() + "đ) của SP ID " + variant.getId());
                }

                // Kiểm tra Tồn kho
                if (dto.getQuantityLimit() > variant.getStock()) {
                    throw new RuntimeException("Lỗi: Số lượng Sale (" + dto.getQuantityLimit() + ") vượt quá Tồn Kho hiện tại (" + variant.getStock() + ") của SP ID " + variant.getId());
                }

                // 3. CHẶN LỖI: XUNG ĐỘT KHUNG GIỜ (TRÙNG ĐỢT SALE KHÁC)
                // Lúc tạo mới thì Flash Sale chưa có ID trong DB, ta gán tạm là -1L để query không bị lỗi
                long overlapCount = flashSaleItemRepository.countOverlappingSales(
                        variant.getId(),
                        request.getStartTime(),
                        request.getEndTime(),
                        -1L
                );

                if (overlapCount > 0) {
                    throw new RuntimeException("Lỗi: Phiên bản SP ID " + variant.getId() + " đang tham gia một Flash Sale khác trong cùng khung giờ này!");
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
        return flashSaleRepository.findAll();
    }

    @Transactional
    public FlashSale updateFlashSale(Long id, FlashSaleRequestDTO request) {
        // GỌI HÀM KIỂM TRA NGHIỆP VỤ
        validateFlashSaleLogic(request);

        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt Flash Sale này!"));

        flashSale.setName(request.getName());
        flashSale.setStartTime(request.getStartTime());
        flashSale.setEndTime(request.getEndTime());

        flashSale.getItems().clear();

        if (request.getItems() != null) {
            List<FlashSaleItem> newItems = request.getItems().stream().map(dto -> {
                ProductVariant variant = productVariantRepository.findById(dto.getVariantId())
                        .orElseThrow(() -> new RuntimeException("Phiên bản SP ID " + dto.getVariantId() + " không tồn tại!"));

                // Kiểm tra giá Sale
                if (dto.getSalePrice().compareTo(variant.getPrice()) >= 0) {
                    throw new RuntimeException("Lỗi: Giá Sale (" + dto.getSalePrice() + "đ) phải nhỏ hơn Giá Gốc (" + variant.getPrice() + "đ) của SP ID " + variant.getId());
                }

                // Kiểm tra Tồn kho
                if (dto.getQuantityLimit() > variant.getStock()) {
                    throw new RuntimeException("Lỗi: Số lượng Sale (" + dto.getQuantityLimit() + ") vượt quá Tồn Kho hiện tại (" + variant.getStock() + ") của SP ID " + variant.getId());
                }

                // 3. CHẶN LỖI: XUNG ĐỘT KHUNG GIỜ (TRÙNG ĐỢT SALE KHÁC)
                // Lúc update, bỏ qua chính cái ID của Flash Sale đang sửa
                long overlapCount = flashSaleItemRepository.countOverlappingSales(
                        variant.getId(),
                        request.getStartTime(),
                        request.getEndTime(),
                        flashSale.getId()
                );

                if (overlapCount > 0) {
                    throw new RuntimeException("Lỗi: Phiên bản SP ID " + variant.getId() + " đang tham gia một Flash Sale khác trong cùng khung giờ này!");
                }

                FlashSaleItem item = new FlashSaleItem();
                item.setFlashSale(flashSale);
                item.setProductVariant(variant);
                item.setSalePrice(dto.getSalePrice());
                item.setQuantityLimit(dto.getQuantityLimit());
                return item;
            }).collect(Collectors.toList());

            flashSale.getItems().addAll(newItems);
        }

        return flashSaleRepository.save(flashSale);
    }

    @Transactional
    public void deleteFlashSale(Long id) {
        flashSaleRepository.deleteById(id);
    }
}