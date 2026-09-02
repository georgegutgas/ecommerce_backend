package com.ecommercefesti.ecommerce_backend.repository;

import com.ecommercefesti.ecommerce_backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findBySlug(String slug);
    // Método con convención de nombres de Spring Data JPA
    List<Category> findByParentIsNull();

    // O si prefieres una consulta explícita JPQL con FETCH para evitar N+1 queries:
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subcategories WHERE c.parent IS NULL")
    List<Category> findRootCategoriesWithSubcategories();
}