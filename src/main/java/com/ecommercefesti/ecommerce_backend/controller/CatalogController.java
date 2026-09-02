package com.ecommercefesti.ecommerce_backend.controller;

import com.ecommercefesti.ecommerce_backend.dto.*;
import com.ecommercefesti.ecommerce_backend.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    /*
    @GetMapping("/products")
    public ResponseEntity<Page<ProductCatalogResponse>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(catalogService.getAllProducts(pageable));
    }
     */

    @GetMapping("/products")
    public ResponseEntity<Page<ProductCatalogResponse>> getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive,
            Pageable pageable
    ) {
        return ResponseEntity.ok(catalogService.getProducts(categoryId, minPrice, maxPrice, isActive, pageable));
    }

    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<Page<ProductCatalogResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(catalogService.getProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductCatalogResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getProductById(id));
    }

    // GET por Slug (Público)
    @GetMapping("/products/slug/{slug}")
    public ResponseEntity<ProductCatalogResponse> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(catalogService.getProductBySlug(slug));
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCatalogResponse> createProduct(@Valid @RequestBody ProductCatalogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogService.createProduct(request));
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCatalogResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductCatalogRequest request
    ) {
        return ResponseEntity.ok(catalogService.updateProduct(id, request));
    }

    @PatchMapping("/products/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCatalogResponse> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        return ResponseEntity.ok(catalogService.updateStock(id, request));
    }

    @PostMapping("/products/{id}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCatalogResponse> addImage(
            @PathVariable Long id,
            @RequestParam String imageUrl
    ) {
        return ResponseEntity.ok(catalogService.updateImageUrl(id, imageUrl));
    }

    @DeleteMapping("/products/{id}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCatalogResponse> removeImage(
            @PathVariable Long id,
            @RequestParam String imageUrl
    ) {
        return ResponseEntity.ok(catalogService.updateImageUrl(id, imageUrl));
    }

//    @DeleteMapping("/products/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
//        catalogService.deleteProduct(id);
//        return ResponseEntity.noContent().build();
//    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        catalogService.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }
}