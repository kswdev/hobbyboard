package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.event.dto.event.EventForm;
import com.hobbyboard.domain.event.entity.Enrollment;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.event.service.EnrollmentReadService;
import com.hobbyboard.domain.event.service.EnrollmentWriteService;
import com.hobbyboard.domain.event.service.EventReadService;
import com.hobbyboard.domain.event.service.EventWriteService;
import com.hobbyboard.domain.study.event.EnrollmentAcceptEvent;
import com.hobbyboard.domain.study.event.EnrollmentEvent;
import com.hobbyboard.domain.study.event.EnrollmentRejectEvent;
import com.hobbyboard.domain.study.event.StudyUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class EventEnrollmentUsacase {

    private final ModelMapper modelMapper;
    private final EventReadService eventReadService;
    private final EnrollmentReadService enrollmentReadService;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public void enrollment(Long eventId, AccountDto accountDto) {
        Event event = eventReadService.findWithEnrollmentsById(eventId).orElseThrow();

        Account account = modelMapper.map(accountDto, Account.class);

        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .account(account)
                .enrolledAt(LocalDateTime.now())
                .build();

        if (event.canSubmit(account))
            event.submitEnrollment(enrollment);
    }

    @Transactional
    public void disenrollment(Long id, AccountDto accountDto) {
        Event event = eventReadService.findWithEnrollmentsById(id).orElseThrow();

        Account account = modelMapper.map(accountDto, Account.class);

        Enrollment enrollment = enrollmentReadService.findByEventAndAccount(event, account);

        if (!ObjectUtils.isEmpty(enrollment))
            event.disenrollment(enrollment);
    }

    @Transactional
    public void updateEvent(Long id, EventForm eventForm) {
        Event event = eventReadService.findWithEnrollmentsById(id).orElseThrow();

        modelMapper.map(eventForm, event);

        if (event.getRestOfCanBeAccepted() > 0)
            event.acceptRestOfEnrollments();

        eventPublisher.publishEvent(new StudyUpdatedEvent(event.getStudy(),
                "'" + event.getTitle() + "' 모임 정보를 수정했습니다."));
    }

    @Transactional
    public void acceptEnrollment(AccountDto accountDto, Long eventId, Long enrollmentId) {
        Event event = eventReadService.findWithEnrollmentsById(eventId).orElseThrow();
        Enrollment enrollment = enrollmentReadService.findById(enrollmentId);
        Account account = modelMapper.map(accountDto, Account.class);

        event.acceptEnrollment(account, enrollment);

        eventPublisher.publishEvent(new EnrollmentAcceptEvent(enrollment));
    }

    @Transactional
    public void rejectEnrollment(AccountDto accountDto, Long eventId, Long enrollmentId) {
        Event event = eventReadService.findWithEnrollmentsById(eventId).orElseThrow();
        Enrollment enrollment = enrollmentReadService.findById(enrollmentId);
        Account account = modelMapper.map(accountDto, Account.class);

        event.rejectEnrollment(account, enrollment);

        eventPublisher.publishEvent(new EnrollmentRejectEvent(enrollment));
    }

    @Transactional
    public void checkInEnrollment(AccountDto accountDto, Long eventId, Long enrollmentId) {
        Account account = modelMapper.map(accountDto, Account.class);
        Event event = eventReadService.findById(eventId).orElseThrow();
        Enrollment enrollment = enrollmentReadService.findById(enrollmentId);

        event.checkIn(account, enrollment);
    }

    @Transactional
    public void checkOutEnrollment(AccountDto accountDto, Long eventId, Long enrollmentId) {
        Account account = modelMapper.map(accountDto, Account.class);
        Event event = eventReadService.findById(eventId).orElseThrow();
        Enrollment enrollment = enrollmentReadService.findById(enrollmentId);

        event.checkOut(account, enrollment);
    }
}
