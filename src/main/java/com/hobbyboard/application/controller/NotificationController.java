package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.notification.dto.NotificationDTO;
import com.hobbyboard.domain.notification.dto.Notifications;
import com.hobbyboard.domain.notification.entity.Notification;
import com.hobbyboard.domain.notification.repository.NotificationRepository;
import com.hobbyboard.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final ModelMapper modelMapper;
    private final NotificationService service;
    private final NotificationRepository repository;

    @GetMapping("/notifications")
    public String getNotifications(@CurrentUser AccountDto accountDto, Model model) {
        Account account = modelMapper.map(accountDto, Account.class);

        List<NotificationDTO> uncheckedNotificationList = service.findByAccountAndCheckedOrderByCreatedAtDesc(account, false).stream()
                .map(this::toNotificationDTO)
                .toList();

        Notifications notifications = Notifications.from(uncheckedNotificationList);

        List<NotificationDTO> uncheckedNotifications = notifications.getNotifications();

        long NumberOfChecked = repository.countByAccountAndChecked(account, true);

        putCategorizedNotifications(model, notifications, uncheckedNotifications.size(), NumberOfChecked);

        model.addAttribute("isNew", true);

        //service.markAsRead(uncheckedNotifications.stream().map((notificationDTO) -> modelMapper.map(notificationDTO, Notification.class)).toList());
        return "notification/list";
    }

    private NotificationDTO toNotificationDTO(Notification notification) {
        return modelMapper.map(notification, NotificationDTO.class);
    }

    @GetMapping("/notifications/old")
    public String getOldNotifications(@CurrentUser AccountDto accountDto, Model model) {
        Account account = modelMapper.map(accountDto, Account.class);

        List<NotificationDTO> checkedNotificationList = service.findByAccountAndCheckedOrderByCreatedAtDesc(account, true).stream()
                .map(this::toNotificationDTO)
                .toList();

        Notifications checkedNotifications = Notifications.from(checkedNotificationList);

        List<NotificationDTO> checkedNotification = checkedNotifications.getNotifications();

        long numberOfNotChecked = repository.countByAccountAndChecked(account, false);

        putCategorizedNotifications(model, checkedNotifications, numberOfNotChecked, checkedNotification.size());
        model.addAttribute("isNew", false);
        return "notification/list";
    }

    @DeleteMapping("/notifications")
    public String deleteNotifications(@CurrentUser AccountDto account) {
        repository.deleteByAccountAndChecked(modelMapper.map(account, Account.class), true);
        return "redirect:/notifications";
    }

    private void putCategorizedNotifications(Model model, Notifications notifications,
                                             long numberOfNotChecked, long numberOfChecked) {

        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("notifications", notifications.getNotifications());
        model.addAttribute("newStudyNotifications", notifications.getNewStudyNotifications());
        model.addAttribute("watchingStudyNotifications", notifications.getWatchingStudyNotifications());
        model.addAttribute("eventEnrollmentNotifications", notifications.getEventEnrollmentNotifications());
    }
}
