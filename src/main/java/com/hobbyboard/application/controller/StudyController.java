package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.application.usacase.StudyAccountUsacase;
import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.study.dto.StudyForm;
import com.hobbyboard.domain.study.dto.StudyFormValidator;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.service.StudyReadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Controller
public class StudyController {

    private final StudyAccountUsacase studyAccountUsacase;
    private final StudyFormValidator studyFormValidator;

    @InitBinder("studyForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyFormValidator);
    }

    static final String STUDY = "study";
    static final String FORM = "/form";

    @GetMapping("/study/{path}/join")
    public String joinStudy(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path
    ) {
        studyAccountUsacase.joinStudy(accountDto, path);
        return "redirect:/study/" + path + "/members";
    }

    @GetMapping("/study/{path}/leave")
    public String leaveStudy(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path
    ) {
        studyAccountUsacase.leaveStudy(accountDto, path);
        return "redirect:/study/" + path + "/members";
    }

    @GetMapping("/new-study")
    public String newStudyForm(
            @CurrentUser AccountDto accountDto,
            Model model
    ) {
        model.addAttribute("account", accountDto);
        model.addAttribute(new StudyForm());
        return STUDY + FORM;
    }

    @PostMapping("/new-study")
    public String newStudySubmit(
            @CurrentUser AccountDto accountDto,
            @Valid StudyForm studyForm,
            BindingResult errors
    ) {
        if (errors.hasErrors())
            return "study/form";

        Study study = studyAccountUsacase.createNewStudy(studyForm, accountDto);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String viewStudy(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            Model model
    ) {
        model.addAttribute("account", accountDto);
        model.addAttribute("study", studyAccountUsacase.findByPath(path));
        return "study/view";
    }

    @GetMapping("/study/{path}/members")
    public String viewStudyMembers(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            Model model
    ) {
        model.addAttribute("account", accountDto);
        //TODO: 스터디에 속한 회원만 조회하므로 필요없는 EAGER FETCH 해제하기
        model.addAttribute("study", studyAccountUsacase.findByPath(path));

        return "study/members";
    }
}
