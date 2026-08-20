package com.marketmax.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class StatsDTOs {

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class SummaryStats {
        private BigDecimal totalRevenue;
        private Long totalOrders;
        private Long totalUsers;
        private Long totalProducts;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class SalesByDay {
        private String date;
        private BigDecimal total;
        private Long count;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class TopProduct {
        private String productId;
        private String title;
        private Long quantitySold;
        private BigDecimal revenue;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class TopUser {
        private Long userId;
        private String name;
        private BigDecimal totalSpent;
        private Long ordersCount;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class StatusCount {
        private String status;
        private Long count;
    }
}
