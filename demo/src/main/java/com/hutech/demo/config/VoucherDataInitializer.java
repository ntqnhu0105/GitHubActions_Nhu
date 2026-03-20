package com.hutech.demo.config;

import com.hutech.demo.model.Voucher;
import com.hutech.demo.repository.IVoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/** Thêm voucher mẫu khi ứng dụng khởi động (chỉ khi chưa có). */
@Component
@RequiredArgsConstructor
public class VoucherDataInitializer {

    private final IVoucherRepository voucherRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (voucherRepository.count() > 0) return;
        List<Voucher> defaults = List.of(
                Voucher.builder()
                        .name("Giảm 10.000đ")
                        .description("Giảm 10.000đ cho đơn hàng")
                        .discountAmount(10_000L)
                        .pointsRequired(10)
                        .active(true)
                        .build(),
                Voucher.builder()
                        .name("Giảm 20.000đ")
                        .description("Giảm 20.000đ cho đơn hàng")
                        .discountAmount(20_000L)
                        .pointsRequired(20)
                        .active(true)
                        .build()
        );
        voucherRepository.saveAll(defaults);
    }
}
