package com.marketmax.controller;

import com.marketmax.dto.OrderDTO;
import com.marketmax.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gerenciamento de pedidos e checkout")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/api/orders")
    @Operation(summary = "Listar pedidos do usuário")
    public ResponseEntity<List<OrderDTO>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    @PostMapping("/api/checkout")
    @Operation(summary = "Realizar checkout do carrinho")
    public ResponseEntity<Map<String, Object>> checkout() {
        OrderDTO order = orderService.checkout();
        return ResponseEntity.ok(Map.of("success", true, "order", order));
    }
}
