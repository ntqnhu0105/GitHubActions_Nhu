package com.hutech.demo.service;

import com.hutech.demo.model.CartItem;
import com.hutech.demo.model.Order;
import com.hutech.demo.model.OrderDetail;
import com.hutech.demo.repository.OrderDetailRepository;
import com.hutech.demo.repository.OrderRepository;
import com.hutech.demo.repository.IUserRepository;
import com.hutech.demo.repository.IUserVoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartService cartService;
    private final IUserRepository userRepository;
    private final IUserVoucherRepository userVoucherRepository;

    @Transactional
    public Order createOrder(String customerName, String phone, String address, String note, String paymentMethod,
                             Double totalAmount, Double vipDiscount, Double voucherDiscount, Double earnedVipPoints,
                             Long userVoucherId, List<CartItem> cartItems) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNote(note);
        order.setPaymentMethod(paymentMethod);
        order.setTotalAmount(totalAmount);
        order.setVipDiscount(vipDiscount != null ? vipDiscount : 0.0);
        order.setVoucherDiscount(voucherDiscount != null ? voucherDiscount : 0.0);
        order.setEarnedVipPoints(earnedVipPoints);

        order = orderRepository.save(order);

        // Đánh dấu voucher đã sử dụng (nếu có) và kiểm tra thuộc user đăng nhập
        if (userVoucherId != null && voucherDiscount != null && voucherDiscount > 0) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String username = auth.getName();
                userRepository.findByUsername(username).flatMap(user ->
                    userVoucherRepository.findById(userVoucherId)
                            .filter(uv -> uv.getUser().getId().equals(user.getId()))
                            .filter(uv -> !Boolean.TRUE.equals(uv.getUsed()))
                ).ifPresent(uv -> {
                    uv.setUsed(true);
                    userVoucherRepository.save(uv);
                });
            }
        }

        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
        }

        // Cộng điểm tích lũy Quà Tặng VIP vào user (chỉ USER) sau khi đặt hàng thành công
        int pointsToAdd = earnedVipPoints != null ? (int) Math.round(earnedVipPoints) : 0;
        if (pointsToAdd > 0) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
                String username = auth.getName();
                userRepository.findByUsername(username).ifPresent(user -> {
                    if (user.getRoles().stream().anyMatch(r -> "USER".equals(r.getName()))) {
                        user.setPoints((user.getPoints() != null ? user.getPoints() : 0) + pointsToAdd);
                        userRepository.save(user);
                    }
                });
            }
        }

        cartService.clearCart();
        return order;
    }
}
