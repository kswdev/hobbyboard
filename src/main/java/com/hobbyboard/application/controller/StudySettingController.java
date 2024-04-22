package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.application.usacase.StudyAccountUsacase;
import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.study.dto.StudyDescriptionForm;
import com.hobbyboard.domain.study.dto.StudyDto;
import com.hobbyboard.domain.study.dto.StudyForm;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.service.StudyWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
@Controller
public class StudySettingController {

    private final StudyWriteService studyWriteService;
    private final StudyAccountUsacase studyAccountUsacase;
    private final ModelMapper modelMapper;

    @GetMapping("/description")
    public String viewStudySetting(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            Model model
    ) {
        StudyDto studyDto   = studyAccountUsacase.getStudyToUpdate(accountDto, path);
        StudyForm studyForm = modelMapper.map(studyDto, StudyForm.class);

        model.addAttribute("account", accountDto);
        model.addAttribute("study", studyDto);
        model.addAttribute("studyDescriptionForm", studyForm);

        return "study/settings/description";
    }

    @PostMapping("/description")
    public String updateStudyInfo(
            @Valid StudyDescriptionForm studyForm,
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            RedirectAttributes attributes,
            BindingResult errors,
            Model model
    ) {
        if (errors.hasErrors()) {
            model.addAttribute("account", accountDto);
            model.addAttribute("studyDescriptionForm", studyForm);
            return "study/settings/description";
        }

        Study updatedStudy = studyWriteService.updateStudyDescription(path, studyForm);
        attributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");
        return "redirect:/study/" + getEncode(updatedStudy) + "/settings/description";
    }

    private static String getEncode(Study updatedStudy) {
        return URLEncoder.encode(updatedStudy.getPath(), StandardCharsets.UTF_8);
    }
}
