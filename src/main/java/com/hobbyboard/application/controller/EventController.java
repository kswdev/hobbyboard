package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.application.usacase.EventEnrollmentUsacase;
import com.hobbyboard.application.usacase.StudyAccountUsacase;
import com.hobbyboard.application.usacase.StudyEventUsacase;
import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.event.dto.event.EventDto;
import com.hobbyboard.domain.event.dto.event.EventForm;
import com.hobbyboard.domain.event.dto.event.EventFormValidator;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.event.service.EventReadService;
import com.hobbyboard.domain.event.service.EventWriteService;
import com.hobbyboard.domain.study.dto.StudyDto;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.service.StudyReadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
@Controller
public class EventController {

    private final StudyEventUsacase studyEventUsacase;
    private final StudyAccountUsacase studyAccountUsacase;
    private final EventEnrollmentUsacase eventEnrollmentUsacase;
    private final StudyReadService studyReadService;
    private final EventWriteService eventWriteService;
    private final EventReadService eventReadService;
    private final EventFormValidator eventFormValidator;
    private final ModelMapper modelMapper;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventFormValidator);
    }

    @PostMapping("/events/{id}/enroll")
    public String enrollEvent(
            @PathVariable Long id,
            @PathVariable String path,
            @CurrentUser AccountDto accountDto
    ) {
        eventEnrollmentUsacase.eventEnrollment(id, accountDto);
        return "redirect:/study/" + path + "/events/" + id;
    }
    @DeleteMapping("/events/{id}")
    public String cancelEvent(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            @PathVariable Long id
    ) {
        Study study = studyReadService.findWithAccountByPath(path);
        studyAccountUsacase.checkIfManager(accountDto, study);
        eventWriteService.delete(eventReadService.findById(id).orElseThrow());
        return "redirect:/study/" + StudyDto.from(study).getEncodePath() + "/events";
    }

    @GetMapping("/events/{id}/edit")
    public String updateEventForm(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            @PathVariable Long id,
            Model model
    ) {

        StudyDto studyDto = StudyDto.fromWithAll(studyReadService.findWithAllByPath(path));
        EventDto eventDto = EventDto.from(eventReadService.findById(id).orElseThrow());

        model.addAttribute(modelMapper.map(eventDto, EventForm.class));
        model.addAttribute("study", studyDto);
        model.addAttribute("event", eventDto);
        model.addAttribute("account", accountDto);
        return "event/update-form";
    }
    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            @PathVariable Long id,
            @Valid EventForm eventForm,
            BindingResult errors,
            Model model
    ) {
        StudyDto study = StudyDto.fromWithAll(studyReadService.findWithAllByPath(path));
        EventDto event = studyEventUsacase.findWithEnrollmentById(id);

        eventFormValidator.validateUpdateForm(eventForm, event, errors);

        if (errors.hasErrors()) {
            model.addAttribute(eventForm);
            model.addAttribute("event", event);
            model.addAttribute("study", study);
            model.addAttribute("account", accountDto);
            return "event/update-form";
        }

        studyEventUsacase.updateEvent(id, eventForm);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
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

        log.info("studyDto ; {}", studyDto);
        List<EventDto> newEvents = new ArrayList<>();
        List<EventDto> oldEvents = new ArrayList<>();

        studyEventUsacase.findByStudyIdOrderByStartDateTime(studyDto.getId())
                .forEach((event) -> {
                    if (event.getEndDateTime().isBefore(LocalDateTime.now()))
                        oldEvents.add(event);
                    else
                        newEvents.add(event);
                });

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);
        model.addAttribute("account", accountDto);
        model.addAttribute("study", studyDto);
        return "study/events";
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
