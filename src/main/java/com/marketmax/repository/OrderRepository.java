package com.marketmax.repository;

import com.marketmax.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    List<Order> findAllByOrderByOrderDateDesc();

    @Query("SELECT FUNCTION('DATE', o.orderDate), SUM(o.totalAmount), COUNT(o) " +
           "FROM Order o GROUP BY FUNCTION('DATE', o.orderDate) ORDER BY FUNCTION('DATE', o.orderDate)")
    List<Object[]> aggregateSalesByDay();

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();

    @Query("SELECT o.user.id, o.user.name, SUM(o.totalAmount), COUNT(o) " +
           "FROM Order o GROUP BY o.user.id, o.user.name ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> aggregateSalesByUser();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    BigDecimal sumTotalRevenue();
}
