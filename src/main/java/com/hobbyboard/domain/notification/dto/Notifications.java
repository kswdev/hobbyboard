package com.hobbyboard.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class Notifications {

    @Builder.Default
    private List<NotificationDTO> notifications                = new ArrayList<>();
    @Builder.Default
    private List<NotificationDTO> newStudyNotifications        = new ArrayList<>();
    @Builder.Default
    private List<NotificationDTO> watchingStudyNotifications   = new ArrayList<>();
    @Builder.Default
    private List<NotificationDTO> eventEnrollmentNotifications = new ArrayList<>();

    public static Notifications from (List<NotificationDTO> notifications) {

        List<NotificationDTO> newStudyNotifications        = new ArrayList<>();
        List<NotificationDTO> watchingStudyNotifications   = new ArrayList<>();
        List<NotificationDTO> eventEnrollmentNotifications = new ArrayList<>();

        for (var notification : notifications) {
            switch (notification.getNotificationType()) {
                case STUDY_CREATED    -> newStudyNotifications.add(notification);
                case STUDY_UPDATED    -> watchingStudyNotifications.add(notification);
                case EVENT_ENROLLMENT -> eventEnrollmentNotifications.add(notification);
            }
        }

        return Notifications.builder()
                .notifications(notifications)
                .newStudyNotifications(newStudyNotifications)
                .watchingStudyNotifications(watchingStudyNotifications)
                .eventEnrollmentNotifications(eventEnrollmentNotifications)
                .build();
    }
}
