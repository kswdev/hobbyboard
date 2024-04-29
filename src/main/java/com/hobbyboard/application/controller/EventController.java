package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.application.usacase.StudyEventUsacase;
import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.event.dto.event.EventDto;
import com.hobbyboard.domain.event.dto.event.EventForm;
import com.hobbyboard.domain.event.dto.event.EventFormValidator;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.study.dto.StudyDto;
import com.hobbyboard.domain.study.service.StudyReadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/study/{path}")
@Controller
public class EventController {

    private final StudyEventUsacase studyEventUsacase;
    private final StudyReadService studyReadService;
    private final EventFormValidator eventFormValidator;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventFormValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            Model model
    ) {

        StudyDto study = StudyDto.fromWithAll(studyReadService.findWithAllByPath(path));

        model.addAttribute(new EventForm());
        model.addAttribute("account", accountDto);
        model.addAttribute("study", study);
        return "event/form";
    }

    @GetMapping("/events/{id}")
    public String getEvent(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            @PathVariable Long id,
            Model model
    ) {

        StudyDto study = StudyDto.fromWithAll(studyReadService.findWithAllByPath(path));
        EventDto event = studyEventUsacase.findWithEnrollmentById(id);

        model.addAttribute("event", event);
        model.addAttribute("account", accountDto);
        model.addAttribute("study", study);
        return "event/view";
    }

    @GetMapping("/events")
    public String viewStudyEvents(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            Model model
    ) {
        StudyDto studyDto = StudyDto.fromWithAll(studyReadService.findWithAllByPath(path));

        List<EventDto> newEvents = new ArrayList<>();
        List<EventDto> oldEvents = new ArrayList<>();

        studyEventUsacase.findByStudyIdOrderByStartDateTime(studyDto.getId())
                .forEach((event) -> {
                    if (event.getEndDateTime().isBefore(LocalDateTime.now()))
                        oldEvents.add(EventDto.from(event));
                    else
                        newEvents.add(EventDto.from(event));
                });

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);
        model.addAttribute("account", accountDto);
        model.addAttribute("study", studyDto);
        return "study/events";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            @Valid EventForm eventForm,
            BindingResult errors,
            Model model
    ) {
        StudyDto study = StudyDto.fromWithAll(studyReadService.findWithAllByPath(path));

        if (errors.hasErrors()) {
            model.addAttribute(eventForm);
            model.addAttribute("study", study);
            model.addAttribute("account", accountDto);
            return "event/form";
        }

        Event event = studyEventUsacase.createEvent(eventForm, accountDto, study);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }
}