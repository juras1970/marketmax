package com.marketmax.controller.admin;

import com.marketmax.dto.ProductDTO;
import com.marketmax.dto.admin.ProductAdminRequest;
import com.marketmax.service.admin.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "Admin - Produtos", description = "CRUD de produtos (somente administrador)")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping
    @Operation(summary = "Listar todos os produtos (admin)")
    public ResponseEntity<List<ProductDTO>> findAll() {
        return ResponseEntity.ok(adminProductService.findAll());
    }

    @PostMapping
    @Operation(summary = "Criar novo produto")
    public ResponseEntity<ProductDTO> create(@RequestBody ProductAdminRequest request) {
        return ResponseEntity.ok(adminProductService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto existente")
    public ResponseEntity<ProductDTO> update(@PathVariable String id, @RequestBody ProductAdminRequest request) {
        return ResponseEntity.ok(adminProductService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produto")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        adminProductService.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
