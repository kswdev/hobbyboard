package com.hobbyboard.domain.notification.entity;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.notification.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity @Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Notification {

    @Id @GeneratedValue
    private Long id;
    private String title;
    private String link;
    private String message;
    private boolean checked;

    @ManyToOne
    private Account account;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationType type;
}
