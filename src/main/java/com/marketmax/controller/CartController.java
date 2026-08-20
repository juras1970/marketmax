package com.marketmax.controller;

import com.marketmax.dto.CartItemDTO;
import com.marketmax.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Carrinho", description = "Gerenciamento do carrinho de compras")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Obter itens do carrinho")
    public ResponseEntity<List<CartItemDTO>> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping
    @Operation(summary = "Adicionar ou atualizar item no carrinho")
    public ResponseEntity<List<CartItemDTO>> addToCart(@RequestBody Map<String, Object> body) {
        String productId = (String) body.get("productId");
        int quantity = body.containsKey("quantity") ? ((Number) body.get("quantity")).intValue() : 1;
        boolean replaceQty = body.containsKey("replaceQty") && (Boolean) body.get("replaceQty");

        return ResponseEntity.ok(cartService.addOrUpdateItem(productId, quantity, replaceQty));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover item do carrinho por ID")
    public ResponseEntity<List<CartItemDTO>> removeItem(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.removeItem(id));
    }

    @DeleteMapping
    @Operation(summary = "Esvaziar o carrinho")
    public ResponseEntity<List<CartItemDTO>> clearCart() {
        return ResponseEntity.ok(cartService.clearCart());
    }
}
