package com.marketmax.security;

import com.marketmax.exception.ResourceNotFoundException;
import com.marketmax.model.User;
import com.marketmax.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    /** Retorna o ID do usuário autenticado atualmente (via JWT). */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserDetails details)) {
            throw new ResourceNotFoundException("Usuário não autenticado");
        }
        return details.getId();
    }

    /** Retorna a entidade User completa do usuário autenticado atualmente. */
    public User getCurrentUser() {
        Long id = getCurrentUserId();
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}
