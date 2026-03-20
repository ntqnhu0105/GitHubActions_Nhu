package com.hutech.demo.service;

import com.hutech.demo.model.Product;
import com.hutech.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Retrieve a product by its id
    public Optional<Product> getProductById(@NonNull Long id) {
        return productRepository.findById(id);
    }

    // Add a new product to the database
    public Product addProduct(@NonNull Product product) {
        return productRepository.save(product);
    }

    // Update an existing product
    public Product updateProduct(@NonNull Product product) {
        Long id = product.getId();
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        id + " does not exist."));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setImage(product.getImage());
        return productRepository.save(existingProduct);
    }

    // Delete a product by its id
    public void deleteProductById(@NonNull Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }
}
