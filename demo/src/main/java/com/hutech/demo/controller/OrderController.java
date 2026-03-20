package com.hutech.demo.controller;

import com.hutech.demo.model.CartItem;
import com.hutech.demo.model.UserVoucher;
import com.hutech.demo.service.CartService;
import com.hutech.demo.service.OrderService;
import com.hutech.demo.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;
    private final PointsService pointsService;

    @GetMapping("/checkout")
    public String checkout(Authentication auth, @RequestParam(value = "userVoucherId", required = false) Long preSelectedVoucherId, Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cartItems", cartItems);
        List<UserVoucher> myVouchers = (auth != null && auth.isAuthenticated())
                ? pointsService.getMyVouchers(auth.getName()).stream()
                    .filter(uv -> !Boolean.TRUE.equals(uv.getUsed()))
                    .collect(Collectors.toList())
                : List.of();
        model.addAttribute("myVouchers", myVouchers);
        model.addAttribute("preSelectedVoucherId", preSelectedVoucherId);
        return "cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(
            Authentication auth,
            @RequestParam("customerName") String customerName,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam(value = "useVipPoints", defaultValue = "false") boolean useVipPoints,
            @RequestParam(value = "userVoucherId", required = false) Long requestedUserVoucherId) {

        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        double earnedVipPoints = totalAmount / 10000.0;
        double vipDiscount = useVipPoints ? 50000.0 : 0.0;

        double voucherDiscount = 0.0;
        Long userVoucherId = null;
        if (requestedUserVoucherId != null && requestedUserVoucherId > 0) {
            var uvOpt = pointsService.getUserVoucherByIdAndUsername(requestedUserVoucherId, auth != null ? auth.getName() : null);
            if (uvOpt.isPresent() && !Boolean.TRUE.equals(uvOpt.get().getUsed())) {
                userVoucherId = uvOpt.get().getId();
                voucherDiscount = uvOpt.get().getVoucher().getDiscountAmount().doubleValue();
            }
        }
        orderService.createOrder(customerName, phone, address, note, paymentMethod, totalAmount, vipDiscount, voucherDiscount, earnedVipPoints, userVoucherId, cartItems);
        return "redirect:/order/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Đơn hàng của bạn đã được đặt thành công.");
        return "cart/order-confirmation";
    }
}
