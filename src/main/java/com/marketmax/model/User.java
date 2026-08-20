package com.marketmax.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "email", unique = true, length = 190)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private String role = "USER";

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "level", length = 100)
    private String level;

    @Column(name = "member_since", length = 50)
    private String memberSince;

    @Column(name = "avatar_url", length = 1000)
    private String avatarUrl;

    @Column(name = "coupons_count")
    @Builder.Default
    private Integer couponsCount = 0;

    @Column(name = "coins_count")
    @Builder.Default
    private Integer coinsCount = 0;

    @Column(name = "reset_password_token", length = 255)
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry")
    private java.time.LocalDateTime resetPasswordTokenExpiry;

    @Column(name = "sales_count")
    @Builder.Default
    private Integer salesCount = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "favorites",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    private Set<Product> favorites = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();
}
