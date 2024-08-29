package buiducnhan.hutech.Online_Shopping_Store.api;

import buiducnhan.hutech.Online_Shopping_Store.entities.CartItem;
import buiducnhan.hutech.Online_Shopping_Store.entities.Order;
import buiducnhan.hutech.Online_Shopping_Store.service.CartService;
import buiducnhan.hutech.Online_Shopping_Store.service.OrderService;
import buiducnhan.hutech.Online_Shopping_Store.viewmodels.order.OrderGetVm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;

    // Lấy danh sách tất cả đơn hàng
    @GetMapping
    public ResponseEntity<List<OrderGetVm>> getAllOrders(@RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                    @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Order> orders = orderService.getAllOrders(pageable);

        Page<OrderGetVm> orderGetVms = orders.map(OrderGetVm::from);
        return ResponseEntity.ok(orderGetVms.getContent());
    }

    // Lấy đơn hàng theo ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderGetVm> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        OrderGetVm orderGetVm = OrderGetVm.from(order);
        return ResponseEntity.ok(orderGetVm);
    }

    // Tạo một đơn hàng mới
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestParam("userId") Long userId,
                                              @RequestParam("customerName") String customerName,
                                              @RequestParam("phoneNumber") String phoneNumber,
                                              @RequestParam("address") String address) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm vào giỏ hàng.");
        }
        orderService.createOrder(userId, customerName, phoneNumber, address, cartItems);
        return ResponseEntity.ok("Đơn hàng đã được gửi đi thành công. Bạn sẽ được chuyển hướng đến trang xác nhận.");
    }

    // Xóa đơn hàng
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Đơn hàng đã được xóa thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi xóa đơn hàng: " + e.getMessage());
        }
    }

    // Lấy danh sách đơn hàng của người dùng
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<OrderGetVm>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        List<OrderGetVm> orderGetVmList = orders.stream().map(OrderGetVm::from).toList();
        return new ResponseEntity<>(orderGetVmList, HttpStatus.OK);
    }
}
