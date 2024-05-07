package com.hobbyboard.domain.notification.repository;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByAccountAndChecked(Account account, boolean checked);
}
