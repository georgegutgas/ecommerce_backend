package com.ecommercefesti.ecommerce_backend.service;

import com.ecommercefesti.ecommerce_backend.dto.CategoryRequest;
import com.ecommercefesti.ecommerce_backend.dto.CategoryResponse;
import com.ecommercefesti.ecommerce_backend.dto.CategoryTreeResponse;
import com.ecommercefesti.ecommerce_backend.dto.SubcategoryResponse;
import com.ecommercefesti.ecommerce_backend.entity.Category;
import com.ecommercefesti.ecommerce_backend.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToCategoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        return mapToCategoryResponse(category);
    }

    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con slug: " + slug));
        return mapToCategoryResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getCategoryTree() {
        // 1. Obtenemos solo las categorías principales (parent IS NULL)
        List<Category> rootCategories = categoryRepository.findRootCategoriesWithSubcategories();

        // 2. Mapeamos a la estructura del DTO
        return rootCategories.stream().map(parent -> new CategoryTreeResponse(
                parent.getId(),
                parent.getName(),
                parent.getSlug(),
                parent.getSubcategories().stream().map(sub -> new SubcategoryResponse(
                        sub.getId(),
                        sub.getName(),
                        sub.getSlug()
                )).toList()
        )).toList();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }

        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

        category.setName(request.name());
        category.setDescription(request.description());

        Category updatedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public CategoryResponse mapToCategoryResponse(Category category) {
        if (category == null) return null;
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription()
        );
    }
}