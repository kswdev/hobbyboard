package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.study.dto.StudyForm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class StudyController {

    static final String STUDY = "study";
    static final String FORM = "/form";

    @GetMapping("/new-study")
    public String newStudyForm(
            @CurrentUser AccountDto accountDto,
            Model model
    ) {
        model.addAttribute("account", accountDto);
        model.addAttribute(new StudyForm());
        return STUDY + FORM;
    }
}
