package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.event.entity.Enrollment;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.event.service.EnrollmentWriteService;
import com.hobbyboard.domain.event.service.EventReadService;
import com.hobbyboard.domain.event.service.EventWriteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class EventEnrollmentUsacase {

    private final EventWriteService eventWriteService;
    private final EventReadService eventReadService;
    private final EnrollmentWriteService enrollmentWriteService;
    private final ModelMapper modelMapper;


    @Transactional
    public void eventEnrollment(Long eventId, AccountDto accountDto) {
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
}
