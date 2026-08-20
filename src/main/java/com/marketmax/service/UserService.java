package com.marketmax.service;

import com.marketmax.dto.UserProfileDTO;
import com.marketmax.exception.BusinessException;
import com.marketmax.exception.ResourceNotFoundException;
import com.marketmax.model.Product;
import com.marketmax.model.User;
import com.marketmax.repository.ProductRepository;
import com.marketmax.repository.UserRepository;
import com.marketmax.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CurrentUserProvider currentUserProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserProfileDTO getProfile() {
        return UserProfileDTO.fromEntity(currentUserProvider.getCurrentUser());
    }

    @Transactional
    public UserProfileDTO updateProfile(String name, String avatar) {
        User user = currentUserProvider.getCurrentUser();

        if (StringUtils.hasText(name)) {
            user.setName(name);
        }
        if (StringUtils.hasText(avatar)) {
            user.setAvatarUrl(avatar);
        }

        userRepository.save(user);
        return UserProfileDTO.fromEntity(user);
    }

    @Transactional
    public void changePassword(String currentPassword, String newPassword) {
        User user = currentUserProvider.getCurrentUser();

        if (user.getPasswordHash() == null || !passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BusinessException("Senha atual incorreta");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("A nova senha deve ter pelo menos 6 caracteres");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<String> getFavorites() {
        User user = currentUserProvider.getCurrentUser();
        return user.getFavorites().stream()
            .map(Product::getId)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<String> toggleFavorite(String productId) {
        User user = currentUserProvider.getCurrentUser();

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + productId));

        boolean isFavorited = user.getFavorites().stream()
            .anyMatch(p -> p.getId().equals(productId));

        if (isFavorited) {
            user.getFavorites().removeIf(p -> p.getId().equals(productId));
        } else {
            user.getFavorites().add(product);
        }

        userRepository.save(user);

        return user.getFavorites().stream()
            .map(Product::getId)
            .collect(Collectors.toList());
    }
}
