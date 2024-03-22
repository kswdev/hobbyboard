package com.hobbyboard.controller;

import com.hobbyboard.domain.account.dto.JoinAccountResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/sign-up")
    public String signUp(ModelMap modelMap) {
        modelMap.addAttribute("account", new JoinAccountResponse());
        return "account/sign-up";
    }
}
