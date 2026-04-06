package trungdevcode.latra.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Dto.FlashSaleRequestDTO;
import trungdevcode.latra.Entity.FlashSale;
import trungdevcode.latra.Entity.FlashSaleItem;
import trungdevcode.latra.Entity.Product;
import trungdevcode.latra.Repository.FlashSaleRepository;
import trungdevcode.latra.Repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlashSaleService {

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public FlashSale createFlashSale(FlashSaleRequestDTO request) {
        FlashSale flashSale = new FlashSale();
        flashSale.setName(request.getName());
        flashSale.setStartTime(request.getStartTime());
        flashSale.setEndTime(request.getEndTime());
        flashSale.setStatus(true);

        if (request.getItems() != null) {
            List<FlashSaleItem> items = request.getItems().stream().map(dto -> {
                Product product = productRepository.findById(dto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm ID " + dto.getProductId() + " không tồn tại!"));

                FlashSaleItem item = new FlashSaleItem();
                item.setFlashSale(flashSale);
                item.setProduct(product);
                item.setSalePrice(dto.getSalePrice());
                item.setQuantity(dto.getQuantity());
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
        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt Flash Sale này!"));

        flashSale.setName(request.getName());
        flashSale.setStartTime(request.getStartTime());
        flashSale.setEndTime(request.getEndTime());

        flashSale.getItems().clear();

        if (request.getItems() != null) {
            List<FlashSaleItem> newItems = request.getItems().stream().map(dto -> {
                Product product = productRepository.findById(dto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm ID " + dto.getProductId() + " không tồn tại!"));

                FlashSaleItem item = new FlashSaleItem();
                item.setFlashSale(flashSale);
                item.setProduct(product);
                item.setSalePrice(dto.getSalePrice());
                item.setQuantity(dto.getQuantity());
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
