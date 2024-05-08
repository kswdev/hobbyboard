package com.hobbyboard.domain.notification.dto;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.notification.NotificationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@EqualsAndHashCode(of = "id") @Builder
@AllArgsConstructor @NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private String title;
    private String link;
    private String message;
    private boolean checked;
    private Account account;
    private LocalDateTime createdAt;
    private NotificationType notificationType;
}
