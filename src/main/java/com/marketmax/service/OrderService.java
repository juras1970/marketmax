package com.marketmax.service;

import com.marketmax.dto.OrderDTO;
import com.marketmax.exception.BusinessException;
import com.marketmax.model.*;
import com.marketmax.repository.*;
import com.marketmax.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final NotificationRepository notificationRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrders() {
        Long userId = currentUserProvider.getCurrentUserId();
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId).stream()
            .map(OrderDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO checkout() {
        User user = currentUserProvider.getCurrentUser();

        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new BusinessException("Carrinho está vazio");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(itemTotal);

            String imageUrl = product.getImages().isEmpty() ? "" : product.getImages().get(0).getImageUrl();

            OrderItem orderItem = OrderItem.builder()
                .productId(product.getId())
                .quantity(cartItem.getQuantity())
                .title(product.getTitle())
                .priceAtTime(product.getPrice())
                .imageUrl(imageUrl)
                .build();
            orderItems.add(orderItem);
        }

        String orderId = "order-" + UUID.randomUUID().toString().substring(0, 8);

        Order order = Order.builder()
            .id(orderId)
            .user(user)
            .orderDate(LocalDateTime.now())
            .status("processing")
            .totalAmount(total)
            .items(new ArrayList<>())
            .build();

        for (OrderItem item : orderItems) {
            item.setOrder(order);
            order.getItems().add(item);
        }

        orderRepository.save(order);

        // Limpa o carrinho
        cartItemRepository.deleteByUserId(user.getId());

        // Atualiza contagem de compras do usuário (para nível/gamificação)
        user.setSalesCount((user.getSalesCount() == null ? 0 : user.getSalesCount()) + 1);

        // Cria notificação
        String totalFormatted = "R$ %,.2f".formatted(total).replace(",", "X").replace(".", ",").replace("X", ".");
        Notification notification = Notification.builder()
            .id("notif-" + UUID.randomUUID().toString().substring(0, 8))
            .user(user)
            .title("Pedido Realizado com Sucesso!")
            .message("Seu pedido " + orderId + " de " + totalFormatted + " foi recebido e já está em processamento.")
            .createdAt(LocalDateTime.now())
            .isRead(false)
            .build();
        notificationRepository.save(notification);

        return OrderDTO.fromEntity(order);
    }
}
