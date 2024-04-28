package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.application.usacase.StudyEventUsacase;
import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.event.dto.event.EventDto;
import com.hobbyboard.domain.event.dto.event.EventForm;
import com.hobbyboard.domain.event.dto.event.EventFormValidator;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.event.service.EventReadService;
import com.hobbyboard.domain.study.dto.StudyDto;
import com.hobbyboard.domain.study.service.StudyReadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/study/{path}")
@Controller
public class EventController {

    private final StudyEventUsacase studyEventUsacase;
    private final StudyReadService studyReadService;
    private final EventReadService eventReadService;
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
        EventDto event = studyEventUsacase.findWithEnrollmentByIdAndCheckAccount(id, accountDto);

        model.addAttribute("event", event);
        model.addAttribute("account", accountDto);
        model.addAttribute("study", study);
        return "event/view";
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
