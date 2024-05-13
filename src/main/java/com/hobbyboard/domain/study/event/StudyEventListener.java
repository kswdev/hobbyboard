package com.hobbyboard.domain.study.event;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.entity.AccountTag;
import com.hobbyboard.domain.account.entity.AccountZone;
import com.hobbyboard.domain.account.predicate.AccountPredicates;
import com.hobbyboard.domain.account.repository.AccountRepository;
import com.hobbyboard.domain.account.repository.AccountTagRepository;
import com.hobbyboard.domain.account.repository.AccountZoneRepository;
import com.hobbyboard.domain.event.entity.Enrollment;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.mail.ConsoleMailSender;
import com.hobbyboard.domain.notification.NotificationType;
import com.hobbyboard.domain.notification.entity.Notification;
import com.hobbyboard.domain.notification.repository.NotificationRepository;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyAccount;
import com.hobbyboard.domain.study.entity.StudyTag;
import com.hobbyboard.domain.study.entity.StudyZone;
import com.hobbyboard.domain.study.repository.StudyRepository;
import com.hobbyboard.domain.tag.entity.Tag;
import com.hobbyboard.domain.zone.entity.Zone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final AccountTagRepository accountTagRepository;
    private final NotificationRepository notificationRepository;
    private final AccountZoneRepository accountZoneRepository;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Study study = event.getStudy();

        createNotification(study, account, enrollmentEvent.getMessage(), NotificationType.EVENT_ENROLLMENT);
    }

    @EventListener
    public void handleStudyUpdatedEvent(StudyUpdatedEvent studyUpdatedEvent) {
        Study study = studyRepository.findWithAccountByPath(studyUpdatedEvent.getStudy().getPath());

        study.getStudyAccounts().stream()
                .map(StudyAccount::getAccount)
                .forEach(account -> {
                    if (account.isStudyUpdatedByEmail())
                        sendStudyCreatedEmail(study, account, studyUpdatedEvent.getMessage());

                    if (account.isStudyUpdatedByWeb())
                        createNotification(study, account, studyUpdatedEvent.getMessage(), NotificationType.STUDY_UPDATED);
                });
    }

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyRepository.findWithAllById(studyCreatedEvent.getStudy().getId());

        Set<Tag> tags = study.getTags().stream()
                .map(StudyTag::getTag)
                .collect(Collectors.toUnmodifiableSet());

        Set<Zone> zones = study.getZones().stream()
                .map(StudyZone::getZone)
                .collect(Collectors.toUnmodifiableSet());

        Set<AccountTag> accountTags = accountTagRepository.findByTags(tags);
        Set<AccountZone> accountZones = accountZoneRepository.findByZones(zones);

        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(accountTags, accountZones));
        accounts.forEach(account -> {
            if (account.isStudyCreatedByEmail())
                sendStudyCreatedEmail(study, account, "스터디가 생성되었습니다.");

            if (account.isStudyCreatedByWeb())
                createNotification(study, account, study.getShortDescription(), NotificationType.STUDY_CREATED);
        });
    }

    private void createNotification(Study study, Account account, String message, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .title(study.getTitle())
                .link("/study/" + study.getEncodePath())
                .checked(false)
                .createdAt(LocalDateTime.now())
                .message(message)
                .account(account)
                .notificationType(notificationType)
                .build();

        notificationRepository.save(notification);
    }

    private static void sendStudyCreatedEmail (Study study, Account account, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject(study.getTitle() + message);
        mailMessage.setTo(account.getEmail());
        mailMessage.setText("template");

        ConsoleMailSender consoleMailSender = new ConsoleMailSender();
        consoleMailSender.send(mailMessage);
    }
}
