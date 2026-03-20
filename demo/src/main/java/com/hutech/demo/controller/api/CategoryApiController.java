package com.hutech.demo.controller.api;

import com.hutech.demo.model.Category;
import com.hutech.demo.repository.CategoryRepository;
import com.hutech.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/categories")
public class CategoryApiController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    // GET /api/categories
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllCategories() {
        List<Map<String, Object>> result = categoryService.getAllCategories().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    // GET /api/categories/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable("id") Long id) {
        return categoryService.getCategoryById(id)
                .map(c -> ResponseEntity.ok(toDto(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/categories
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Map<String, Object> body) {
        Category category = new Category();
        category.setName((String) body.get("name"));
        if (body.containsKey("description")) {
            category.setGroupName((String) body.get("description"));
        }
        Category saved = categoryRepository.save(category);
        return ResponseEntity.ok(toDto(saved));
    }

    // PUT /api/categories/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(@PathVariable("id") Long id,
                                                               @RequestBody Map<String, Object> body) {
        return categoryService.getCategoryById(id).map(existing -> {
            if (body.containsKey("name")) existing.setName((String) body.get("name"));
            if (body.containsKey("description")) existing.setGroupName((String) body.get("description"));
            categoryService.updateCategory(existing);
            return ResponseEntity.ok(toDto(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/categories/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/categories - Xóa tất cả danh mục
    @DeleteMapping
    public ResponseEntity<Void> deleteAllCategories() {
        categoryRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

    private Map<String, Object> toDto(Category c) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", c.getId());
        dto.put("name", c.getName());
        // frontend hiển thị "description" -> map từ groupName
        dto.put("description", c.getGroupName() != null ? c.getGroupName() : "");
        dto.put("image", c.getImage());
        return dto;
    }
}
