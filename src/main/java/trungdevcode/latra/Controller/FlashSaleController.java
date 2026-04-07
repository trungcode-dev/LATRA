package trungdevcode.latra.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.FlashSaleRequestDTO;
import trungdevcode.latra.Entity.FlashSale;
import trungdevcode.latra.Service.FlashSaleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flash-sales/admin")
public class FlashSaleController {

    @Autowired
    private FlashSaleService flashSaleService;

    @PostMapping
    public ResponseEntity<FlashSale> createFlashSale(@Valid @RequestBody FlashSaleRequestDTO request) {
        FlashSale response = flashSaleService.createFlashSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FlashSale>> getAllFlashSales() {
        return ResponseEntity.ok(flashSaleService.getAllFlashSales());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlashSale> updateFlashSale(
            @PathVariable Long id,
            @Valid @RequestBody FlashSaleRequestDTO request) {
        return ResponseEntity.ok(flashSaleService.updateFlashSale(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFlashSale(@PathVariable Long id) {
        flashSaleService.deleteFlashSale(id);
        return ResponseEntity.ok("Đã hủy chương trình Flash Sale thành công!");
    }
}
