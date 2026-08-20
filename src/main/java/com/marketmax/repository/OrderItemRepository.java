package com.marketmax.repository;

import com.marketmax.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi.productId, oi.title, SUM(oi.quantity), SUM(oi.priceAtTime * oi.quantity) " +
           "FROM OrderItem oi GROUP BY oi.productId, oi.title ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> aggregateSalesByProduct();
}
