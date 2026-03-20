package com.hutech.demo.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lưu OTP tạm trong bộ nhớ (theo username) để xác thực khi đổi voucher.
 * OTP hết hạn sau 5 phút.
 */
@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALID_MINUTES = 5;

    private final Map<String, OtpSession> store = new ConcurrentHashMap<>();

    public static record OtpSession(String otp, Long voucherId, Instant expiresAt) {}

    /** Tạo OTP 6 chữ số và lưu cho user đổi voucher. */
    public String createAndStore(String username, Long voucherId) {
        String otp = generateOtp();
        Instant expiresAt = Instant.now().plusSeconds(OTP_VALID_MINUTES * 60L);
        store.put(username, new OtpSession(otp, voucherId, expiresAt));
        return otp;
    }

    /** Xác thực OTP và trả về voucherId nếu đúng; sau khi dùng xong nên gọi invalidate. */
    public Optional<Long> verify(String username, String otp) {
        OtpSession session = store.get(username);
        if (session == null) return Optional.empty();
        if (Instant.now().isAfter(session.expiresAt())) {
            store.remove(username);
            return Optional.empty();
        }
        if (!session.otp().equals(otp)) return Optional.empty();
        return Optional.of(session.voucherId());
    }

    /** Xóa OTP sau khi đổi voucher thành công. */
    public void invalidate(String username) {
        store.remove(username);
    }

    private String generateOtp() {
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }
}
