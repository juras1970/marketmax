package com.marketmax.controller;

import com.marketmax.dto.AuthResponse;
import com.marketmax.dto.ForgotPasswordRequest;
import com.marketmax.dto.LoginRequest;
import com.marketmax.dto.RegisterRequest;
import com.marketmax.dto.ResetPasswordRequest;
import com.marketmax.dto.UserProfileDTO;
import com.marketmax.security.CurrentUserProvider;
import com.marketmax.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Cadastro e login de usuários")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/register")
    @Operation(summary = "Cadastrar novo usuário")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e obter token JWT")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário autenticado a partir do token")
    public ResponseEntity<UserProfileDTO> me() {
        return ResponseEntity.ok(UserProfileDTO.fromEntity(currentUserProvider.getCurrentUser()));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar e-mail de redefinição de senha (\"esqueci minha senha\")")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        // Resposta sempre genérica, para não revelar se o e-mail existe na base.
        return ResponseEntity.ok(Map.of("message",
            "Se este e-mail estiver cadastrado, você receberá um link para redefinir sua senha."));
    }

    @GetMapping("/reset-password/validate")
    @Operation(summary = "Verificar se um token de redefinição de senha ainda é válido")
    public ResponseEntity<Map<String, Boolean>> validateResetToken(@RequestParam String token) {
        return ResponseEntity.ok(Map.of("valid", authService.validateResetToken(token)));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Definir uma nova senha a partir do token recebido por e-mail")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso. Você já pode entrar."));
    }
}
