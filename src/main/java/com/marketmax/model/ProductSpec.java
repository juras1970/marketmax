package com.marketmax.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_specs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "spec_key", nullable = false, length = 100)
    private String specKey;

    @Column(name = "spec_value", nullable = false, length = 300)
    private String specValue;
}
