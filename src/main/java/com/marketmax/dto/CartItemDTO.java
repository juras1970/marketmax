package com.marketmax.dto;

import com.marketmax.model.CartItem;
import lombok.Data;

@Data
public class CartItemDTO {

    private String id;
    private String productId;
    private Integer quantity;
    private ProductDTO product;

    public static CartItemDTO fromEntity(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(String.valueOf(item.getId()));
        dto.setProductId(item.getProduct().getId());
        dto.setQuantity(item.getQuantity());
        dto.setProduct(ProductDTO.fromEntity(item.getProduct()));
        return dto;
    }
}
