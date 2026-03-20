package com.hutech.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "voucher")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /** Mô tả ngắn (vd: "Giảm 10.000đ cho đơn hàng"). */
    @Column(name = "description", length = 255)
    private String description;

    /** Số tiền giảm (VNĐ). */
    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    /** Số điểm cần để đổi voucher này. */
    @Column(name = "points_required", nullable = false)
    private Integer pointsRequired;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}
