package com.ecommercefesti.ecommerce_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.regex.Pattern;
import java.text.Normalizer;
import java.util.Locale;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String description;

    // Relación a la categoría Padre
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"subcategories", "parent"})
    private Category parent;

    // Relación a las Subcategorías Hijas
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("parent")
    private List<Category> subcategories;

    @OneToMany(mappedBy = "category")
    @JsonIgnoreProperties("category") // Evita que los productos dentro de la categoría vuelvan a serializar la categoría
    private List<Product> products;

    @PrePersist
    @PreUpdate
    public void generateSlug() {
        if (this.name != null) {
            this.slug = toSlug(this.name);
        }
    }

    private String toSlug(String input) {
        if (input == null) return "";
        Pattern NONLATIN = Pattern.compile("[^\\w-]");
        Pattern WHITESPACE = Pattern.compile("[\\s]");

        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}