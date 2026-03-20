package com.hutech.demo.service;
import com.hutech.demo.model.Category;
import com.hutech.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 * Service class for managing categories.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    /**
     * Retrieve all categories from the database.
     * @return a list of categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    /**
     * Retrieve a category by its id.
     * @param id the id of the category to retrieve
     * @return an Optional containing the found category or empty if not found
     */
    public Optional<Category> getCategoryById(@NonNull Long id) {
        return categoryRepository.findById(id);
    }
    /**
     * Add a new category to the database.
     * @param category the category to add
     */
    public void addCategory(@NonNull Category category) {
        categoryRepository.save(category);
    }
    /**
     * Update an existing category.
     * @param category the category with updated information
     */
    public void updateCategory(@NonNull Category category) {
        Long id = category.getId();
        if (id == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Category with ID " +
                        id + " does not exist."));
        existingCategory.setName(category.getName());
        existingCategory.setGroupName(category.getGroupName());
        categoryRepository.save(existingCategory);
    }

    public void deleteCategoryById(@NonNull Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalStateException("Category with ID " + id + " does not exist.");
        }
        categoryRepository.deleteById(id);
    }
}
