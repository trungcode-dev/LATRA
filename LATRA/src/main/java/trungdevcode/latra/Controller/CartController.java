package trungdevcode.latra.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.CartItemRequestDTO;
import trungdevcode.latra.Service.CartService;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(cartService.getCartByUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/{userId}/add")
    public ResponseEntity<?> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemRequestDTO dto) {
        try {
            return ResponseEntity.ok(cartService.addToCart(userId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/user/{userId}/remove/{cartItemId}")
    public ResponseEntity<?> removeCartItem(
            @PathVariable Long userId,
            @PathVariable Long cartItemId) {
        try {
            return ResponseEntity.ok(cartService.removeCartItem(userId, cartItemId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/user/{userId}/update/{cartItemId}")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        try {
            return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, cartItemId, quantity));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}