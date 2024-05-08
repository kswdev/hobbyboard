package com.hobbyboard.domain.notification.repository;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByAccountAndChecked(Account account, boolean checked);

    List<Notification> findByAccountAndCheckedOrderByCreatedAtDesc(Account account, boolean checked);

    List<Notification> findByAccount(Account account);

    void deleteByAccountAndChecked(Account account, boolean checked);

}
