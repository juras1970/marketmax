package com.marketmax.service;

import com.marketmax.dto.NotificationDTO;
import com.marketmax.model.Notification;
import com.marketmax.repository.NotificationRepository;
import com.marketmax.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotifications() {
        Long userId = currentUserProvider.getCurrentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(NotificationDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<NotificationDTO> markAllAsRead() {
        Long userId = currentUserProvider.getCurrentUserId();
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
        return notifications.stream()
            .map(NotificationDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
