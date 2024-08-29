package buiducnhan.hutech.Online_Shopping_Store.api;

import buiducnhan.hutech.Online_Shopping_Store.service.CartService;
import buiducnhan.hutech.Online_Shopping_Store.viewmodels.cart.CartItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartService cartService;

    @GetMapping("/items")
    public ResponseEntity<List<CartItemDto>> getAllCartItems() {
        try {
            List<CartItemDto> cartItems = cartService.getCartItems().stream()
                    .map(item -> new CartItemDto(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getQuantity(),
                            item.getProduct().getPrice()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping
    public ResponseEntity<String> addProductToCart(@RequestParam("productId") Long productId,
                                                   @RequestParam("quantity") int quantity) {
        try {
            cartService.addToCart(productId, quantity);
            return ResponseEntity.ok("Sản phẩm đã được thêm vào giỏ hàng thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi thêm sản phẩm vào giỏ hàng.");
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long productId) {
        try {
            cartService.removeFromCart(productId);
            return ResponseEntity.ok("Sản phẩm đã được xóa khỏi giỏ hàng.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi xóa sản phẩm khỏi giỏ hàng.");
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        try {
            cartService.clearCart();
            return ResponseEntity.ok("Giỏ hàng đã được làm trống.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi làm trống giỏ hàng.");
        }
    }
}