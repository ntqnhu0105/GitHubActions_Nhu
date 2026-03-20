package com.hutech.demo.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Tên là bắt buộc")
    private String name;
    
    private String groupName; // Group identifier for the mega menu (e.g., "Phụ kiện di động", "Laptop", etc.)
    
    private String image; // Image filename
}