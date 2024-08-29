package buiducnhan.hutech.Online_Shopping_Store.service;

import buiducnhan.hutech.Online_Shopping_Store.exception.InsufficientQuantityException;
import buiducnhan.hutech.Online_Shopping_Store.entities.CartItem;
import buiducnhan.hutech.Online_Shopping_Store.entities.Product;
import buiducnhan.hutech.Online_Shopping_Store.repository.ProductRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {
    @Getter
    private List<CartItem> cartItems = new ArrayList<>();
    @Autowired
    private ProductRepository productRepository;

    public void addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        // Kiểm tra số lượng còn lại
        if (product.getAmount() < quantity) {
            throw new InsufficientQuantityException("Không đủ số lượng sản phẩm để thêm vào giỏ hàng.");
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        CartItem existingItem = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            cartItems.add(new CartItem(product, quantity));
        }
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void clearCart() {
        cartItems.clear();
    }
}

