package com.marketmax.controller;

import com.marketmax.dto.UserProfileDTO;
import com.marketmax.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Usuário", description = "Perfil e favoritos do usuário")
public class UserController {

    private final UserService userService;

    @GetMapping("/api/profile")
    @Operation(summary = "Obter perfil do usuário")
    public ResponseEntity<UserProfileDTO> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @PutMapping("/api/profile")
    @Operation(summary = "Atualizar perfil do usuário")
    public ResponseEntity<UserProfileDTO> updateProfile(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String avatar = body.get("avatar");
        return ResponseEntity.ok(userService.updateProfile(name, avatar));
    }

    @PostMapping("/api/profile/password")
    @Operation(summary = "Alterar senha do usuário autenticado")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> body) {
        userService.changePassword(body.get("currentPassword"), body.get("newPassword"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/api/favorites")
    @Operation(summary = "Listar IDs dos produtos favoritos")
    public ResponseEntity<List<String>> getFavorites() {
        return ResponseEntity.ok(userService.getFavorites());
    }

    @PostMapping("/api/favorites/toggle")
    @Operation(summary = "Adicionar ou remover produto dos favoritos")
    public ResponseEntity<List<String>> toggleFavorite(@RequestBody Map<String, String> body) {
        String productId = body.get("productId");
        return ResponseEntity.ok(userService.toggleFavorite(productId));
    }
}
