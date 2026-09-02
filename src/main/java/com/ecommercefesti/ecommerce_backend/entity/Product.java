package com.ecommercefesti.ecommerce_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("products") // Evita que la categoría vuelva a serializar la lista de productos
    private Category category;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    @PreUpdate
    public void generateSlugAndDates() {
        if (this.createdAt == null) {
            this.createdAt = ZonedDateTime.now();
        }
        if (this.name != null) {
            this.slug = toSlug(this.name);
        }
    }

    private String toSlug(String input) {
        if (input == null) return "";
        java.text.Normalizer.Form form = java.text.Normalizer.Form.NFD;
        String normalized = java.text.Normalizer.normalize(input, form);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized)
                .replaceAll("")
                .toLowerCase(java.util.Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
