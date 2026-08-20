package com.marketmax.controller.admin;

import com.marketmax.dto.OrderDTO;
import com.marketmax.dto.admin.StatsDTOs.*;
import com.marketmax.service.admin.AdminSalesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Vendas", description = "Pedidos e estatísticas de vendas (somente administrador)")
public class AdminSalesController {

    private final AdminSalesService adminSalesService;

    @GetMapping("/orders")
    @Operation(summary = "Listar todos os pedidos de todos os usuários")
    public ResponseEntity<List<OrderDTO>> findAllOrders() {
        return ResponseEntity.ok(adminSalesService.findAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    @Operation(summary = "Atualizar status de um pedido")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminSalesService.updateStatus(id, body.get("status")));
    }

    @GetMapping("/stats/summary")
    @Operation(summary = "Resumo geral: receita, pedidos, usuários e produtos")
    public ResponseEntity<SummaryStats> getSummary() {
        return ResponseEntity.ok(adminSalesService.getSummary());
    }

    @GetMapping("/stats/sales-by-day")
    @Operation(summary = "Vendas agregadas por dia")
    public ResponseEntity<List<SalesByDay>> getSalesByDay() {
        return ResponseEntity.ok(adminSalesService.getSalesByDay());
    }

    @GetMapping("/stats/top-products")
    @Operation(summary = "Produtos mais vendidos")
    public ResponseEntity<List<TopProduct>> getTopProducts() {
        return ResponseEntity.ok(adminSalesService.getTopProducts());
    }

    @GetMapping("/stats/top-users")
    @Operation(summary = "Usuários que mais compraram")
    public ResponseEntity<List<TopUser>> getTopUsers() {
        return ResponseEntity.ok(adminSalesService.getTopUsers());
    }

    @GetMapping("/stats/status-breakdown")
    @Operation(summary = "Distribuição de pedidos por status")
    public ResponseEntity<List<StatusCount>> getStatusBreakdown() {
        return ResponseEntity.ok(adminSalesService.getStatusBreakdown());
    }
}
