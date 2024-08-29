package buiducnhan.hutech.Online_Shopping_Store.service;

import buiducnhan.hutech.Online_Shopping_Store.entities.*;
import buiducnhan.hutech.Online_Shopping_Store.repository.OrderDetailRepository;
import buiducnhan.hutech.Online_Shopping_Store.repository.OrderRepository;
import buiducnhan.hutech.Online_Shopping_Store.repository.ProductRepository;
import buiducnhan.hutech.Online_Shopping_Store.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public void createOrder(Long userId, String customerName, String phoneNumber, String address, List<CartItem> cartItems) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhoneNumber(phoneNumber); // Set phone number
        order.setAddress(address); // Set address
        order.setUser(user);
        order = orderRepository.save(order);

        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);

            // Update product amount
            Product product = item.getProduct();
            product.setAmount(product.getAmount() - item.getQuantity());
            productRepository.save(product);
        }

        cartService.clearCart();
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderRepository.delete(order);
    }
}
