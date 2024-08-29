package buiducnhan.hutech.Online_Shopping_Store.controllers;

import buiducnhan.hutech.Online_Shopping_Store.entities.CartItem;
import buiducnhan.hutech.Online_Shopping_Store.entities.User;
import buiducnhan.hutech.Online_Shopping_Store.service.CartService;
import buiducnhan.hutech.Online_Shopping_Store.service.OrderService;
import buiducnhan.hutech.Online_Shopping_Store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        Optional<User> optionalUser = userService.getCurrentUser();
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
        } else {
            return "users/login";
        }
        return "/cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam("userId") Long userId,
                              @RequestParam("customerName") String customerName,
                              @RequestParam("phoneNumber") String phoneNumber,
                              @RequestParam("address") String address) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        orderService.createOrder(userId, customerName, phoneNumber, address, cartItems);
        return "redirect:/order/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }
}
