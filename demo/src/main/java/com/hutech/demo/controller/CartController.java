package com.hutech.demo.controller;

import com.hutech.demo.model.UserVoucher;
import com.hutech.demo.service.CartService;
import com.hutech.demo.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final PointsService pointsService;

    @GetMapping
    public String showCart(Authentication auth, Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        List<UserVoucher> myVouchers = (auth != null && auth.isAuthenticated())
                ? pointsService.getMyVouchers(auth.getName()).stream()
                    .filter(uv -> !Boolean.TRUE.equals(uv.getUsed()))
                    .collect(Collectors.toList())
                : List.of();
        model.addAttribute("myVouchers", myVouchers);
        return "/cart/cart";
    }

    @GetMapping("/add")
    public String addToCart(@RequestParam @NonNull Long productId, @RequestParam(defaultValue = "1") int quantity) {
        cartService.addToCart(productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable @NonNull Long productId) {
        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam @NonNull Long productId, @RequestParam int quantity) {
        cartService.updateCart(productId, quantity);
        return "redirect:/cart";
    }
}
