package com.hutech.demo.service;

import com.hutech.demo.model.User;
import com.hutech.demo.model.UserVoucher;
import com.hutech.demo.model.Voucher;
import com.hutech.demo.repository.IUserRepository;
import com.hutech.demo.repository.IUserVoucherRepository;
import com.hutech.demo.repository.IVoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointsService {

    private final IUserRepository userRepository;
    private final IVoucherRepository voucherRepository;
    private final IUserVoucherRepository userVoucherRepository;

    public int getPointsByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getPoints)
                .orElse(0);
    }

    /** Lấy email của user (để gửi OTP). */
    public Optional<String> getEmailByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getEmail);
    }

    public List<Voucher> getAvailableVouchers() {
        return voucherRepository.findByActiveTrueOrderByPointsRequiredAsc();
    }

    public List<UserVoucher> getMyVouchers(String username) {
        return userRepository.findByUsername(username)
                .map(userVoucherRepository::findByUserOrderByCreatedAtDesc)
                .orElse(List.of());
    }

    /** Lấy UserVoucher theo id và username (để xác thực khi áp dụng vào đơn hàng). */
    public Optional<UserVoucher> getUserVoucherByIdAndUsername(Long userVoucherId, String username) {
        if (userVoucherId == null || username == null) return Optional.empty();
        return userVoucherRepository.findById(userVoucherId)
                .filter(uv -> username.equals(uv.getUser().getUsername()));
    }

    /**
     * Đổi voucher: trừ điểm user, tạo UserVoucher với code duy nhất.
     * Chỉ user có đủ điểm mới đổi được.
     */
    @Transactional
    public Optional<String> exchangeVoucher(String username, Long voucherId) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<Voucher> voucherOpt = voucherRepository.findById(voucherId);
        if (userOpt.isEmpty() || voucherOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();
        Voucher voucher = voucherOpt.get();
        if (!Boolean.TRUE.equals(voucher.getActive())) return Optional.empty();
        if (user.getPoints() < voucher.getPointsRequired()) return Optional.empty();

        user.setPoints(user.getPoints() - voucher.getPointsRequired());
        userRepository.save(user);

        String code = "V" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        UserVoucher uv = UserVoucher.builder()
                .user(user)
                .voucher(voucher)
                .code(code)
                .used(false)
                .createdAt(java.time.Instant.now())
                .build();
        userVoucherRepository.save(uv);
        return Optional.of(code);
    }
}
