package com.marketmax.controller.admin;

import com.marketmax.dto.CategoryDTO;
import com.marketmax.dto.admin.CategoryAdminRequest;
import com.marketmax.service.admin.AdminCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Admin - Categorias", description = "CRUD de categorias (somente administrador)")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @GetMapping
    @Operation(summary = "Listar todas as categorias (admin)")
    public ResponseEntity<List<CategoryDTO>> findAll() {
        return ResponseEntity.ok(adminCategoryService.findAll());
    }

    @PostMapping
    @Operation(summary = "Criar nova categoria")
    public ResponseEntity<CategoryDTO> create(@RequestBody CategoryAdminRequest request) {
        return ResponseEntity.ok(adminCategoryService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria existente")
    public ResponseEntity<CategoryDTO> update(@PathVariable String id, @RequestBody CategoryAdminRequest request) {
        return ResponseEntity.ok(adminCategoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        adminCategoryService.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
