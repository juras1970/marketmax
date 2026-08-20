package com.marketmax.dto;

import com.marketmax.model.Notification;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Data
public class NotificationDTO {

    private String id;
    private String title;
    private String message;
    private String date;
    private Boolean read;

    public static NotificationDTO fromEntity(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.getIsRead());

        // Format relative date
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime created = notification.getCreatedAt();
        long minutesAgo = ChronoUnit.MINUTES.between(created, now);
        long hoursAgo = ChronoUnit.HOURS.between(created, now);
        long daysAgo = ChronoUnit.DAYS.between(created, now);

        if (minutesAgo < 5) {
            dto.setDate("Agora mesmo");
        } else if (minutesAgo < 60) {
            dto.setDate("Há " + minutesAgo + " minutos");
        } else if (hoursAgo < 24) {
            dto.setDate("Há " + hoursAgo + " hora" + (hoursAgo > 1 ? "s" : ""));
        } else if (daysAgo == 1) {
            dto.setDate("Ontem");
        } else if (daysAgo < 7) {
            dto.setDate("Há " + daysAgo + " dias");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("pt", "BR"));
            dto.setDate(created.format(formatter));
        }

        return dto;
    }
}
