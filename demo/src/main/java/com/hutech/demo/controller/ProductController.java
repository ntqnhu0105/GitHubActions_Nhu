package com.hutech.demo.controller;
import com.hutech.demo.model.Product;
import com.hutech.demo.service.CategoryService;
import com.hutech.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.lang.NonNull;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    
    public static final String UPLOAD_DIRECTORY = com.hutech.demo.config.WebConfig.UPLOAD_DIR;

    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/products-list";
    }
    // For adding a new product
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories()); //Load categories
        return "/products/add-product";
    }
    // Process the form for adding a new product
    @PostMapping("/add")
    public String addProduct(@Valid @NonNull Product product, BindingResult result, @RequestParam("imageFile") MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "/products/add-product";
        }
        if (!imageFile.isEmpty()) {
            try {
                String originalFileName = imageFile.getOriginalFilename();
                String fileName = java.util.UUID.randomUUID().toString();
                if (originalFileName != null && originalFileName.contains(".")) {
                    String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                    fileName += fileExtension;
                }
                
                Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
                Files.createDirectories(fileNameAndPath.getParent());
                Files.write(fileNameAndPath, imageFile.getBytes());
                product.setImage(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        productService.addProduct(product);
        return "redirect:/products";
    }
    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable @NonNull Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories()); //Load categories
        return "/products/update-product";
    }
    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable @NonNull Long id, @Valid @NonNull Product product,
                                BindingResult result, @RequestParam("imageFile") MultipartFile imageFile) {
        if (result.hasErrors()) {
            product.setId(id); // set id to keep it in the form in case of errors
            return "/products/update-product";
        }
        Product existingProduct = productService.getProductById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        if (!imageFile.isEmpty()) {
            try {
                String originalFileName = imageFile.getOriginalFilename();
                String fileName = java.util.UUID.randomUUID().toString();
                if (originalFileName != null && originalFileName.contains(".")) {
                    String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                    fileName += fileExtension;
                }
                
                Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
                Files.createDirectories(fileNameAndPath.getParent());
                Files.write(fileNameAndPath, imageFile.getBytes());
                product.setImage(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
             // Keep existing image if no new one is uploaded
             product.setImage(existingProduct.getImage());
        }
        
        productService.updateProduct(product);
        return "redirect:/products";
    }
    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable @NonNull Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }

    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable @NonNull Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "/products/detail";
    }
}
