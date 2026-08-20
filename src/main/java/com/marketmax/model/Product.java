package com.marketmax.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "discount")
    private Integer discount;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "free_shipping", nullable = false)
    private Boolean freeShipping = false;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "sales_count_text", length = 100)
    private String salesCountText;

    @Column(name = "is_new")
    private Boolean isNew = false;

    @Column(name = "seller_reputation", length = 100)
    private String sellerReputation;

    @Column(name = "seller_rating", length = 20)
    private String sellerRating;

    @Column(name = "seller_sales", length = 50)
    private String sellerSales;

    @Column(name = "seller_posting", length = 50)
    private String sellerPosting;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ProductSpec> specs = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product other = (Product) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
