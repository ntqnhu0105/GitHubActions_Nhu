package com.hutech.demo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Role {
    ADMIN(1),   // Quản trị viên: toàn quyền (sản phẩm + danh mục).
    USER(2),    // Người dùng: chỉ xem sản phẩm và mua hàng.
    MANAGER(3); // Quản lý: chỉ thêm/sửa/xóa/xem sản phẩm, không quản lý danh mục.

    public final long value; // Giá trị id tương ứng trong bảng role.
}
