package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.service.AccountReadService;
import com.hobbyboard.domain.event.dto.event.EnrollmentDto;
import com.hobbyboard.domain.event.dto.event.EventDto;
import com.hobbyboard.domain.event.dto.event.EventForm;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.event.service.EventReadService;
import com.hobbyboard.domain.event.service.EventWriteService;
import com.hobbyboard.domain.study.dto.StudyDto;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.service.StudyReadService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StudyEventUsacase {

    private final AccountReadService accountReadService;
    private final EventWriteService eventWriteService;
    private final EventReadService eventReadService;
    private final ModelMapper modelMapper;

    @Transactional
    public Event createEvent(EventForm eventForm, AccountDto accountDto, StudyDto studyDto) {
        Account account = accountReadService.findByNickname(accountDto.getNickname());

        Study study = modelMapper.map(studyDto, Study.class);

        Event event = Event.builder()
                .study(study)
                .createBy(account)
                .createdDateTime(LocalDateTime.now())
                .build();
        modelMapper.map(eventForm, event);

        return eventWriteService.save(event);
    }

    @Transactional(readOnly = true)
    public EventDto findWithEnrollmentById(Long id) {
        return eventReadService.findById(id)
                .map(EventDto::from)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));
    }

    public EventDto findWithEnrollmentByIdAndCheckAccount(Long id, AccountDto accountDto) {
        Account account = modelMapper.map(accountDto, Account.class);
        Event event = eventReadService.findById(id).orElseThrow();

        EventDto eventDto = EventDto.builder()
                .attended(event.isAttended(account))
                .canAccept(event.canAccept(event.g))
                .build();
    }
}
