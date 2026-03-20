package com.hutech.demo.controller;
import com.hutech.demo.model.Product;
import com.hutech.demo.service.ProductService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public String home(Model model) {
        List<Product> allProducts = productService.getAllProducts();
        List<Product> flashSaleProducts = allProducts.stream()
                .filter(p -> p.getPromotionType() != null && !p.getPromotionType().equals("none"))
                .toList();
        List<Product> regularProducts = allProducts.stream()
                .filter(p -> p.getPromotionType() == null || p.getPromotionType().equals("none"))
                .toList();

        model.addAttribute("flashSaleProducts", flashSaleProducts);
        model.addAttribute("regularProducts", regularProducts);
        return "home/home";
    }
}