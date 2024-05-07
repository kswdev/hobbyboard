package com.hobbyboard.domain.study.event;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.entity.AccountTag;
import com.hobbyboard.domain.account.entity.AccountZone;
import com.hobbyboard.domain.account.predicate.AccountPredicates;
import com.hobbyboard.domain.account.repository.AccountRepository;
import com.hobbyboard.domain.account.repository.AccountTagRepository;
import com.hobbyboard.domain.account.repository.AccountZoneRepository;
import com.hobbyboard.domain.mail.ConsoleMailSender;
import com.hobbyboard.domain.notification.NotificationType;
import com.hobbyboard.domain.notification.entity.Notification;
import com.hobbyboard.domain.notification.repository.NotificationRepository;
import com.hobbyboard.domain.study.entity.Study;
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
    private final NotificationRepository notificationRepository;
    private final AccountTagRepository accountTagRepository;
    private final AccountZoneRepository accountZoneRepository;

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
                sendStudyCreatedEmail(study, account);


            if (account.isStudyCreatedByWeb())
                saveStudyCreatedNotification(study, account);
        });
    }

    private void saveStudyCreatedNotification(Study study, Account account) {
        Notification notification = Notification.builder()
                .title(study.getTitle())
                .link("/study/" + study.getEncodePath())
                .checked(false)
                .createdAt(LocalDateTime.now())
                .message(study.getShortDescription())
                .account(account)
                .type(NotificationType.STUDY_CREATED)
                .build();

        notificationRepository.save(notification);
    }

    private static void sendStudyCreatedEmail(Study study, Account account) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject(study.getTitle() + "스터디가 생겼습니다.");
        mailMessage.setTo(account.getEmail());
        mailMessage.setText("template");

        ConsoleMailSender consoleMailSender = new ConsoleMailSender();
        consoleMailSender.send(mailMessage);
    }
}
