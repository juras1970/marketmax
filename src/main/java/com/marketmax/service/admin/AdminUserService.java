package com.marketmax.service.admin;

import com.marketmax.dto.admin.UserAdminDTO;
import com.marketmax.dto.admin.UserAdminRequest;
import com.marketmax.exception.BusinessException;
import com.marketmax.exception.ResourceNotFoundException;
import com.marketmax.model.User;
import com.marketmax.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private static final Set<String> VALID_ROLES = Set.of("USER", "ADMIN");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserAdminDTO> findAll() {
        return userRepository.findAll().stream()
            .map(UserAdminDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public UserAdminDTO create(UserAdminRequest request) {
        if (!StringUtils.hasText(request.getName()) || !StringUtils.hasText(request.getEmail())
            || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("Nome, e-mail e senha são obrigatórios");
        }
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Já existe uma conta com este e-mail");
        }
        if (request.getPassword().length() < 6) {
            throw new BusinessException("A senha deve ter pelo menos 6 caracteres");
        }

        String role = normalizeRole(request.getRole());

        User user = User.builder()
            .name(request.getName().trim())
            .email(email)
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role(role)
            .active(request.getActive() == null || request.getActive())
            .level(StringUtils.hasText(request.getLevel()) ? request.getLevel() : "Nível Bronze")
            .couponsCount(0)
            .coinsCount(0)
            .salesCount(0)
            .build();

        userRepository.save(user);
        return UserAdminDTO.fromEntity(user);
    }

    public UserAdminDTO update(Long id, UserAdminRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName().trim());
        }
        if (StringUtils.hasText(request.getEmail())) {
            String email = request.getEmail().trim().toLowerCase();
            if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new BusinessException("Já existe uma conta com este e-mail");
            }
            user.setEmail(email);
        }
        if (StringUtils.hasText(request.getPassword())) {
            if (request.getPassword().length() < 6) {
                throw new BusinessException("A senha deve ter pelo menos 6 caracteres");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (StringUtils.hasText(request.getRole())) {
            user.setRole(normalizeRole(request.getRole()));
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        if (StringUtils.hasText(request.getLevel())) {
            user.setLevel(request.getLevel());
        }

        userRepository.save(user);
        return UserAdminDTO.fromEntity(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        }
        userRepository.deleteById(id);
    }

    private String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) return "USER";
        String upper = role.trim().toUpperCase();
        if (!VALID_ROLES.contains(upper)) {
            throw new BusinessException("Papel inválido. Use USER ou ADMIN.");
        }
        return upper;
    }
}
