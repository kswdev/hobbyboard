package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.domain.account.dto.account.AccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class NotificationController {

    @GetMapping("/notifications")
    public String notificationView(
            @CurrentUser AccountDto accountDto,
            Model model
    ) {

        model.addAttribute("account", accountDto);
        return "notification/list";
    }
}
