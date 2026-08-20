package com.marketmax.service;

import com.marketmax.dto.AuthResponse;
import com.marketmax.dto.ForgotPasswordRequest;
import com.marketmax.dto.LoginRequest;
import com.marketmax.dto.RegisterRequest;
import com.marketmax.dto.ResetPasswordRequest;
import com.marketmax.dto.UserProfileDTO;
import com.marketmax.exception.BusinessException;
import com.marketmax.exception.ResourceNotFoundException;
import com.marketmax.model.User;
import com.marketmax.repository.UserRepository;
import com.marketmax.security.AppUserDetails;
import com.marketmax.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${marketmax.reset-token.expiration-minutes}")
    private long resetTokenExpirationMinutes;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!StringUtils.hasText(request.getName()) ||
            !StringUtils.hasText(request.getEmail()) ||
            !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("Nome, e-mail e senha são obrigatórios");
        }

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Já existe uma conta cadastrada com este e-mail");
        }

        if (request.getPassword().length() < 6) {
            throw new BusinessException("A senha deve ter pelo menos 6 caracteres");
        }

        User user = User.builder()
            .name(request.getName().trim())
            .email(email)
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role("USER")
            .active(true)
            .level("Nível Bronze")
            .memberSince(currentMonthYear())
            .couponsCount(1)
            .coinsCount(0)
            .salesCount(0)
            .build();

        userRepository.save(user);

        String token = jwtService.generateToken(new AppUserDetails(user));
        return new AuthResponse(token, UserProfileDTO.fromEntity(user));
    }

    public AuthResponse login(LoginRequest request) {
        if (!StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("E-mail e senha são obrigatórios");
        }

        String email = request.getEmail().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new BusinessException("E-mail ou senha inválidos, ou conta desativada");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new BusinessException("Esta conta está desativada. Entre em contato com o suporte.");
        }

        String token = jwtService.generateToken(new AppUserDetails(user));
        return new AuthResponse(token, UserProfileDTO.fromEntity(user));
    }

    /**
     * Inicia o fluxo de "esqueci minha senha": gera um token de uso único,
     * grava no usuário com prazo de validade e dispara o e-mail com o link.
     * Por segurança, não revela se o e-mail existe ou não na base.
     */
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        if (!StringUtils.hasText(request.getEmail())) {
            throw new BusinessException("Informe o e-mail cadastrado");
        }

        String email = request.getEmail().trim().toLowerCase();

        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(resetTokenExpirationMinutes));
            userRepository.save(user);

            try {
                emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), token);
            } catch (Exception ex) {
                // Não propaga falha de envio de e-mail como erro de negócio: a resposta ao
                // cliente permanece genérica para não revelar detalhes de infraestrutura.
                log.error("Não foi possível enviar o e-mail de redefinição de senha", ex);
            }
        });
    }

    /**
     * Verifica se um token de redefinição de senha ainda é válido (existe e não expirou).
     */
    @Transactional(readOnly = true)
    public boolean validateResetToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        return userRepository.findByResetPasswordToken(token)
            .filter(user -> user.getResetPasswordTokenExpiry() != null
                && user.getResetPasswordTokenExpiry().isAfter(LocalDateTime.now()))
            .isPresent();
    }

    /**
     * Conclui o fluxo de "esqueci minha senha": valida o token, define a nova senha
     * e invalida o token para que não possa ser reutilizado.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!StringUtils.hasText(request.getToken()) || !StringUtils.hasText(request.getNewPassword())) {
            throw new BusinessException("Token e nova senha são obrigatórios");
        }

        if (request.getNewPassword().length() < 6) {
            throw new BusinessException("A senha deve ter pelo menos 6 caracteres");
        }

        User user = userRepository.findByResetPasswordToken(request.getToken())
            .orElseThrow(() -> new BusinessException("Link de redefinição inválido ou já utilizado"));

        if (user.getResetPasswordTokenExpiry() == null
            || user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Link de redefinição expirado. Solicite um novo.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
    }

    private String currentMonthYear() {
        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        java.time.LocalDate now = java.time.LocalDate.now();
        return meses[now.getMonthValue() - 1] + " " + now.getYear();
    }
}
