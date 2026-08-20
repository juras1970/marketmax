package com.marketmax.service.admin;

import com.marketmax.dto.OrderDTO;
import com.marketmax.dto.admin.StatsDTOs.*;
import com.marketmax.exception.BusinessException;
import com.marketmax.exception.ResourceNotFoundException;
import com.marketmax.model.Order;
import com.marketmax.repository.OrderItemRepository;
import com.marketmax.repository.OrderRepository;
import com.marketmax.repository.ProductRepository;
import com.marketmax.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminSalesService {

    private static final Set<String> VALID_STATUSES = Set.of("processing", "shipped", "delivered", "cancelled");

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<OrderDTO> findAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc().stream()
            .map(OrderDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public OrderDTO updateStatus(String orderId, String status) {
        if (!StringUtils.hasText(status) || !VALID_STATUSES.contains(status.toLowerCase())) {
            throw new BusinessException("Status inválido. Use: processing, shipped, delivered ou cancelled");
        }
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com id: " + orderId));
        order.setStatus(status.toLowerCase());
        orderRepository.save(order);
        return OrderDTO.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public SummaryStats getSummary() {
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();
        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        return new SummaryStats(totalRevenue, totalOrders, totalUsers, totalProducts);
    }

    @Transactional(readOnly = true)
    public List<SalesByDay> getSalesByDay() {
        return orderRepository.aggregateSalesByDay().stream()
            .map(row -> new SalesByDay(String.valueOf(row[0]), (BigDecimal) row[1], (Long) row[2]))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopProduct> getTopProducts() {
        return orderItemRepository.aggregateSalesByProduct().stream()
            .limit(10)
            .map(row -> new TopProduct((String) row[0], (String) row[1], (Long) row[2], (BigDecimal) row[3]))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopUser> getTopUsers() {
        return orderRepository.aggregateSalesByUser().stream()
            .limit(10)
            .map(row -> new TopUser((Long) row[0], (String) row[1], (BigDecimal) row[2], (Long) row[3]))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StatusCount> getStatusBreakdown() {
        return orderRepository.countByStatus().stream()
            .map(row -> new StatusCount((String) row[0], (Long) row[1]))
            .collect(Collectors.toList());
    }
}
