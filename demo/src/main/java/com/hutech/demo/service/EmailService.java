package com.hutech.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Gửi email OTP khi user đổi voucher (xác thực qua email đăng ký).
 * Chỉ tạo bean khi có JavaMailSender trên classpath (spring-boot-starter-mail).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnClass(name = "org.springframework.mail.javamail.JavaMailSender")
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    /** Gửi mã OTP đến email của user. */
    public void sendOtpEmail(String toEmail, String otp) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("Chưa cấu hình spring.mail.username — bỏ qua gửi email. OTP (chỉ để test): {}", otp);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject("Mã xác thực đổi voucher - Điểm tích lũy");
            msg.setText(
                "Xin chào,\n\n"
                + "Mã xác thực OTP của bạn là: " + otp + "\n\n"
                + "Mã có hiệu lực trong 5 phút. Không chia sẻ mã này với bất kỳ ai.\n\n"
                + "Trân trọng."
            );
            mailSender.send(msg);
            log.info("Đã gửi OTP đến {}", toEmail);
        } catch (Exception e) {
            log.error("Lỗi gửi email OTP đến {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Không thể gửi email xác thực. Vui lòng thử lại sau.", e);
        }
    }
}
