package com.hobbyboard.domain.event.dto.event;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventFormValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return EventForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventForm eventForm = (EventForm) target;

        if (eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now()))
            errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "모임 접수 종료 일시를 정확히 입력하세요.");

        if (eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime()) ||
            eventForm.getEndDateTime().isBefore(eventForm.getEndEnrollmentDateTime()))
            errors.rejectValue("endDateTime", "wrong.datetime", "모임 접수 종료 일시를 정확히 입력하세요.");

        if (eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime()))
            errors.rejectValue("startDateTime", "wrong.datetime", "모임 접수 시작 일시를 정확히 입력하세요.");
    }
}
