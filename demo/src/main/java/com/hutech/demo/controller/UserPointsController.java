package com.hutech.demo.controller;

import com.hutech.demo.model.UserVoucher;
import com.hutech.demo.model.Voucher;
import com.hutech.demo.service.EmailService;
import com.hutech.demo.service.OtpService;
import com.hutech.demo.service.PointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/user/points")
@RequiredArgsConstructor
public class UserPointsController {

    private final PointsService pointsService;
    private final OtpService otpService;
    private final Optional<EmailService> emailService;

    /** Trang Điểm tích lũy: tra cứu điểm & đổi voucher. Chỉ dành cho USER đã đăng nhập. */
    @GetMapping
    public String page(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = auth.getName();
        int points = pointsService.getPointsByUsername(username);
        List<Voucher> vouchers = pointsService.getAvailableVouchers();
        List<UserVoucher> myVouchers = pointsService.getMyVouchers(username);
        String emailMasked = pointsService.getEmailByUsername(username)
                .map(UserPointsController::maskEmail)
                .orElse("");

        model.addAttribute("totalPoints", points);
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("myVouchers", myVouchers);
        model.addAttribute("userEmailMasked", emailMasked);
        return "users/points";
    }

    /** Gửi mã OTP đến email đăng ký của user (AJAX). */
    @PostMapping("/exchange/send-otp/{voucherId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendOtp(
            Authentication auth,
            @PathVariable Long voucherId
    ) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Vui lòng đăng nhập."));
        }
        String username = auth.getName();
        Optional<String> emailOpt = pointsService.getEmailByUsername(username);
        if (emailOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Tài khoản chưa có email."));
        }
        int points = pointsService.getPointsByUsername(username);
        Optional<Voucher> voucherOpt = pointsService.getAvailableVouchers().stream()
                .filter(v -> v.getId().equals(voucherId))
                .findFirst();
        if (voucherOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Voucher không hợp lệ."));
        }
        Voucher v = voucherOpt.get();
        if (points < v.getPointsRequired()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Điểm không đủ để đổi voucher này."));
        }
        String otp = otpService.createAndStore(username, voucherId);
        if (emailService.isPresent()) {
            emailService.get().sendOtpEmail(emailOpt.get(), otp);
        } else {
            log.warn("EmailService không có (chưa cấu hình mail). OTP để test: {}", otp);
        }
        String masked = maskEmail(emailOpt.get());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Mã OTP đã gửi đến email " + masked + ". Vui lòng kiểm tra hộp thư (và thư mục spam).",
                "emailMasked", masked
        ));
    }

    /** Xác thực OTP và đổi voucher. */
    @PostMapping("/exchange/confirm")
    public String confirmExchange(
            Authentication auth,
            @RequestParam Long voucherId,
            @RequestParam String otp,
            RedirectAttributes redirectAttributes
    ) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = auth.getName();
        Optional<Long> verifiedVoucherId = otpService.verify(username, otp != null ? otp.trim() : "");
        if (verifiedVoucherId.isEmpty() || !verifiedVoucherId.get().equals(voucherId)) {
            redirectAttributes.addFlashAttribute("exchangeError", true);
            redirectAttributes.addFlashAttribute("otpError", "Mã OTP không đúng hoặc đã hết hạn. Vui lòng thử lại.");
            return "redirect:/user/points";
        }
        var codeOpt = pointsService.exchangeVoucher(username, voucherId);
        otpService.invalidate(username);
        if (codeOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("exchangeSuccess", true);
            redirectAttributes.addFlashAttribute("voucherCode", codeOpt.get());
        } else {
            redirectAttributes.addFlashAttribute("exchangeError", true);
        }
        return "redirect:/user/points";
    }

    private static String maskEmail(String email) {
        if (email == null || email.length() < 3) return "***";
        int at = email.indexOf('@');
        if (at <= 0) return "***@***";
        String local = email.substring(0, Math.min(2, at));
        String domain = email.substring(at);
        return local + "***" + domain;
    }
}
