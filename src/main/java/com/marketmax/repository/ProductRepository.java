package com.marketmax.repository;

import com.marketmax.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByCategory(String category);

    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchByTitleOrDescription(@Param("query") String query);

    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "p.category = :category")
    List<Product> searchByQueryAndCategory(@Param("query") String query, @Param("category") String category);

    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> countProductsByCategory();

    List<Product> findByDiscountGreaterThanEqualOrderByDiscountDesc(Integer discount);
}
