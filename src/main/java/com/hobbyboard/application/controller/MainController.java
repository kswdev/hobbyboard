package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.notification.repository.NotificationRepository;
import com.hobbyboard.domain.study.dto.StudyDto;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.service.StudyReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final StudyReadService studyReadService;

    @GetMapping
    public String home(@CurrentUser AccountDto account, Model model) {
        if (account != null) {
            model.addAttribute("account", account);
        }

        return "index";
    }

    @GetMapping("login")
    public String login() {
        return "login";
    }

    @GetMapping("/search/study")
    public String searchStudy(
            @CurrentUser AccountDto accountDtp,
            String keyword,
            Model model
    ) {

        List<StudyDto> study = studyReadService.findByKeyword(keyword);
        model.addAttribute("studyPage", new PageImpl<>(study));
        model.addAttribute("account", accountDtp);
        return "search";
    }
}
