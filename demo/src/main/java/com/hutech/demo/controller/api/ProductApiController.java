package com.hutech.demo.controller.api;

import com.hutech.demo.model.Category;
import com.hutech.demo.model.Product;
import com.hutech.demo.service.CategoryService;
import com.hutech.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // GET /api/products - Lấy tất cả sản phẩm
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        List<Map<String, Object>> result = productService.getAllProducts().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    // GET /api/products/{id} - Lấy sản phẩm theo id
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable("id") Long id) {
        return productService.getProductById(id)
                .map(p -> ResponseEntity.ok(toDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/products - Thêm sản phẩm mới
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Map<String, Object> body) {
        Product product = fromDto(body);
        Product saved = productService.addProduct(product);
        return ResponseEntity.ok(toDto(saved));
    }

    // PUT /api/products/{id} - Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable("id") Long id,
                                                              @RequestBody Map<String, Object> body) {
        return productService.getProductById(id).map(existing -> {
            if (body.containsKey("name"))        existing.setName((String) body.get("name"));
            if (body.containsKey("price"))       existing.setPrice(((Number) body.get("price")).doubleValue());
            if (body.containsKey("description")) existing.setDescription((String) body.get("description"));
            if (body.containsKey("categoryId") && body.get("categoryId") != null) {
                Long catId = ((Number) body.get("categoryId")).longValue();
                categoryService.getCategoryById(catId).ifPresent(existing::setCategory);
            }
            Product updated = productService.updateProduct(existing);
            return ResponseEntity.ok(toDto(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/products/{id} - Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        try {
            productService.deleteProductById(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/products - Xóa tất cả sản phẩm
    @DeleteMapping
    public ResponseEntity<Void> deleteAllProducts() {
        productService.getAllProducts().forEach(p -> productService.deleteProductById(p.getId()));
        return ResponseEntity.ok().build();
    }

    // Chuyển Product entity -> DTO map (trả về categoryId cho frontend)
    private Map<String, Object> toDto(Product p) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", p.getId());
        dto.put("name", p.getName());
        dto.put("price", p.getPrice());
        dto.put("description", p.getDescription());
        dto.put("image", p.getImage());
        dto.put("categoryId", p.getCategory() != null ? p.getCategory().getId() : null);
        dto.put("categoryName", p.getCategory() != null ? p.getCategory().getName() : null);
        dto.put("promotionType", p.getPromotionType());
        dto.put("discount", p.getDiscount());
        dto.put("promotionInfo", p.getPromotionInfo());
        return dto;
    }

    // Chuyển request body -> Product entity
    private Product fromDto(Map<String, Object> body) {
        Product product = new Product();
        if (body.containsKey("name"))        product.setName((String) body.get("name"));
        if (body.containsKey("price"))       product.setPrice(((Number) body.get("price")).doubleValue());
        if (body.containsKey("description")) product.setDescription((String) body.get("description"));
        if (body.containsKey("categoryId") && body.get("categoryId") != null) {
            Long catId = ((Number) body.get("categoryId")).longValue();
            categoryService.getCategoryById(catId).ifPresent(product::setCategory);
        }
        return product;
    }
}
