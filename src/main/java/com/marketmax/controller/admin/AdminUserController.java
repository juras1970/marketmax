package com.marketmax.controller.admin;

import com.marketmax.dto.admin.UserAdminDTO;
import com.marketmax.dto.admin.UserAdminRequest;
import com.marketmax.service.admin.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - Usuários", description = "CRUD de usuários (somente administrador)")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "Listar todos os usuários")
    public ResponseEntity<List<UserAdminDTO>> findAll() {
        return ResponseEntity.ok(adminUserService.findAll());
    }

    @PostMapping
    @Operation(summary = "Criar novo usuário")
    public ResponseEntity<UserAdminDTO> create(@RequestBody UserAdminRequest request) {
        return ResponseEntity.ok(adminUserService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário existente")
    public ResponseEntity<UserAdminDTO> update(@PathVariable Long id, @RequestBody UserAdminRequest request) {
        return ResponseEntity.ok(adminUserService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        adminUserService.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
