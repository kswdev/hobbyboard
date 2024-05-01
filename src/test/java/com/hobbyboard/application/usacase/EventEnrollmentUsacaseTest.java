package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.event.entity.Enrollment;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.event.service.EventReadService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("dev")
class EventEnrollmentUsacaseTest {

    @Autowired
    private EventReadService eventReadService;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void givenEventIdAndAccountDtoWhenEnrollmentEventThenEnrollment() {

        Long eventId = 52L;
        AccountDto accountDto = AccountDto.builder()
                .id(1L)
                .email("seonwongim@gmail.com")
                .build();

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