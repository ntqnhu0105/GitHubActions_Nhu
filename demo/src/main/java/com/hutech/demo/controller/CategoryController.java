package com.hutech.demo.controller;
import com.hutech.demo.model.Category;
import com.hutech.demo.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
@Controller
@RequiredArgsConstructor
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;
    public static final String UPLOAD_DIRECTORY = com.hutech.demo.config.WebConfig.UPLOAD_DIR;

    @GetMapping("/categories/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "/categories/add-category";
    }

    @PostMapping("/categories/add")
    public String addCategory(@Valid @NonNull Category category, BindingResult result, @RequestParam("imageFile") MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "/categories/add-category";
        }
        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
                Files.createDirectories(fileNameAndPath.getParent());
                Files.write(fileNameAndPath, imageFile.getBytes());
                category.setImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        categoryService.addCategory(category);
        return "redirect:/categories";
    }

    // Hiển thị danh sách danh mục
    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "/categories/categories-list";
    }


    // GET request to show category edit form
    @GetMapping("/categories/edit/{id}")
    public String showUpdateForm(@PathVariable("id") @NonNull Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:"
                        + id));
        model.addAttribute("category", category);
        return "/categories/update-category";
    }
    // POST request to update category
    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable("id") @NonNull Long id, @Valid @NonNull Category category,
                                 BindingResult result, Model model, @RequestParam("imageFile") MultipartFile imageFile) {
        if (result.hasErrors()) {
            category.setId(id);
            return "/categories/update-category";
        }
        Category existingCategory = categoryService.getCategoryById(id).orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));

        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
                Files.createDirectories(fileNameAndPath.getParent());
                Files.write(fileNameAndPath, imageFile.getBytes());
                category.setImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
             // Keep existing image if no new one is uploaded
             category.setImage(existingCategory.getImage());
        }
        categoryService.updateCategory(category);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/categories";
    }
    // GET request for deleting category
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") @NonNull Long id, Model model) {
        if (!categoryService.getCategoryById(id).isPresent()) {
            throw new IllegalArgumentException("Invalid category Id:" + id);
        }
        categoryService.deleteCategoryById(id);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/categories";
    }

}
