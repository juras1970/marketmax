package com.marketmax.controller;

import com.marketmax.dto.NotificationDTO;
import com.marketmax.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Gerenciamento de notificações do usuário")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Listar notificações do usuário")
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        return ResponseEntity.ok(notificationService.getNotifications());
    }

    @PostMapping("/read")
    @Operation(summary = "Marcar todas as notificações como lidas")
    public ResponseEntity<List<NotificationDTO>> markAllAsRead() {
        return ResponseEntity.ok(notificationService.markAllAsRead());
    }
}
