package com.hutech.demo.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private String phone;
    private String address;
    private String note;
    private String paymentMethod;
    
    // Billing info
    private Double totalAmount;
    private Double vipDiscount;
    /** Giảm giá từ voucher đổi bằng điểm (mã giảm giá). */
    private Double voucherDiscount;
    private Double earnedVipPoints;
    
    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;
}