package com.hobbyboard.domain.notification.service;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.notification.entity.Notification;
import com.hobbyboard.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void markAsRead(List<Notification> notifications) {
        notifications.forEach(notification -> notification.setChecked(true));
        notificationRepository.saveAll(notifications);
    }

    public List<Notification> findByAccount(Account account) {
        return notificationRepository.findByAccount(account);
    }

    public List<Notification> findByAccountAndCheckedOrderByCreatedAtDesc(Account account, boolean checked) {
        return notificationRepository.findByAccountAndCheckedOrderByCreatedAtDesc(account, checked);
    }
}
