package com.marketmax.dto;

import com.marketmax.model.Order;
import com.marketmax.model.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Data
public class OrderDTO {

    private String id;
    private String date;
    private String status;
    private BigDecimal total;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private String productId;
        private Integer quantity;
        private String title;
        private BigDecimal price;
        private String image;

        public static OrderItemDTO fromEntity(OrderItem item) {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());
            dto.setTitle(item.getTitle());
            dto.setPrice(item.getPriceAtTime());
            dto.setImage(item.getImageUrl());
            return dto;
        }
    }

    public static OrderDTO fromEntity(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("pt", "BR"));
        dto.setDate(order.getOrderDate().format(formatter));
        dto.setStatus(order.getStatus());
        dto.setTotal(order.getTotalAmount());
        dto.setItems(order.getItems().stream()
            .map(OrderItemDTO::fromEntity)
            .collect(Collectors.toList()));

        return dto;
    }
}
