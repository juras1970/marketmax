package com.marketmax.service;

import com.marketmax.dto.CartItemDTO;
import com.marketmax.exception.ResourceNotFoundException;
import com.marketmax.model.CartItem;
import com.marketmax.model.Product;
import com.marketmax.model.User;
import com.marketmax.repository.CartItemRepository;
import com.marketmax.repository.ProductRepository;
import com.marketmax.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public List<CartItemDTO> getCart() {
        Long userId = currentUserProvider.getCurrentUserId();
        return cartItemRepository.findByUserId(userId).stream()
            .map(CartItemDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<CartItemDTO> addOrUpdateItem(String productId, int quantity, boolean replaceQty) {
        User user = currentUserProvider.getCurrentUser();

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + productId));

        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(user.getId(), productId);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = replaceQty ? quantity : item.getQuantity() + quantity;
            if (newQty <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(newQty);
                cartItemRepository.save(item);
            }
        } else {
            if (quantity > 0) {
                CartItem newItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(quantity)
                    .build();
                cartItemRepository.save(newItem);
            }
        }

        return getCart();
    }

    @Transactional
    public List<CartItemDTO> removeItem(Long cartItemId) {
        Long userId = currentUserProvider.getCurrentUserId();
        CartItem item = cartItemRepository.findById(cartItemId)
            .filter(ci -> ci.getUser().getId().equals(userId))
            .orElseThrow(() -> new ResourceNotFoundException("Item do carrinho não encontrado com id: " + cartItemId));
        cartItemRepository.delete(item);
        return getCart();
    }

    @Transactional
    public List<CartItemDTO> clearCart() {
        Long userId = currentUserProvider.getCurrentUserId();
        cartItemRepository.deleteByUserId(userId);
        return getCart();
    }
}
