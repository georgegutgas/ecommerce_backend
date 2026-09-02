package com.ecommercefesti.ecommerce_backend.service;

import com.ecommercefesti.ecommerce_backend.dto.*;
import com.ecommercefesti.ecommerce_backend.entity.Category;
import com.ecommercefesti.ecommerce_backend.entity.Product;
import com.ecommercefesti.ecommerce_backend.repository.CategoryRepository;
import com.ecommercefesti.ecommerce_backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public Page<ProductCatalogResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToProductCatalogResponse);
    }

    public Page<ProductCatalogResponse> getProducts(
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean isActive,
            Pageable pageable
    ) {
        return productRepository.findFilteredProducts(categoryId, minPrice, maxPrice, isActive, pageable)
                .map(this::mapToProductCatalogResponse);
    }


    @Transactional(readOnly = true)
    public Page<ProductCatalogResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(this::mapToProductCatalogResponse);
    }

    @Transactional(readOnly = true)
    public ProductCatalogResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return mapToProductCatalogResponse(product);
    }

    // --- BÚSQUEDA POR SLUG ---
    @Transactional(readOnly = true)
    public ProductCatalogResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con el slug: " + slug));
        return mapToProductCatalogResponse(product);
    }

    @Transactional
    public ProductCatalogResponse createProduct(ProductCatalogRequest request) {
        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    //.orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + request.categoryId()));
                    .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + request.categoryId()));
        }

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .category(category)
                .imageUrl(request.imageUrls() != null && !request.imageUrls().isEmpty()
                        ? request.imageUrls().get(0)
                        : null)
                .isActive(true)
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToProductCatalogResponse(savedProduct);
    }

    @Transactional
    public ProductCatalogResponse updateProduct(Long id, ProductCatalogRequest request) {
        Product product = productRepository.findById(id)
                //.orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + request.categoryId()));
            product.setCategory(category);
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        if (request.imageUrls() != null && !request.imageUrls().isEmpty()) {
            product.setImageUrl(request.imageUrls().get(0));
        }

        Product updatedProduct = productRepository.save(product);
        return mapToProductCatalogResponse(updatedProduct);
    }

    // --- GESTIÓN DE STOCK ---
    @Transactional
    public ProductCatalogResponse updateStock(Long productId, StockUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        product.setStock(request.stock());
        Product updatedProduct = productRepository.save(product);
        return mapToProductCatalogResponse(updatedProduct);
    }

    // --- GESTIÓN DE IMÁGENES ---
    @Transactional
    public ProductCatalogResponse updateImageUrl(Long productId, String imageUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        product.setImageUrl(imageUrl);
        Product updatedProduct = productRepository.save(product);
        return mapToProductCatalogResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));

        product.setIsActive(false);
        productRepository.save(product);
    }

    private ProductCatalogResponse mapToProductCatalogResponse(Product product) {
        return new ProductCatalogResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSlug(),
                product.getPrice(),
                product.getStock(),
                categoryService.mapToCategoryResponse(product.getCategory()),
                product.getImageUrl() != null ? List.of(product.getImageUrl()) : List.of()
        );
    }
}