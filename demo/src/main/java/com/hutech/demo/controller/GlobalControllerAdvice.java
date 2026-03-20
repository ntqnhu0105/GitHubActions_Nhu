package com.hutech.demo.controller;

import com.hutech.demo.model.CartItem;
import com.hutech.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final CartService cartService;

    @ModelAttribute("cartSize")
    public int getCartSize() {
        return cartService.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
    }
}
