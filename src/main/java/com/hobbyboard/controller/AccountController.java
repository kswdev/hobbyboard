package com.hobbyboard.controller;

import com.hobbyboard.domain.account.dto.SignUpForm;
import com.hobbyboard.domain.account.dto.SignUpFormValidator;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.repository.AccountRepository;
import com.hobbyboard.domain.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@RequiredArgsConstructor
@Controller
@SessionAttributes("signUpForm")
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {

        //키 값이 카멜 케이스로 모델 객체에 저장됨
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    /*
     * @Valid 통한 표준 애너테이션(JSR-303) 검사
     * @InitBinder 통한 SignUpForm 파라미터 받을 때 이메일, 닉네임 유니크 검사
     */
    @PostMapping("/sign-up")
    public String signUpSubmit(
        @Valid SignUpForm signUpForm,
        BindingResult result,
        SessionStatus status
    ) {
        if (result.hasErrors())
            return "account/sign-up";

        accountService.processNewAccount(signUpForm);
        status.setComplete();

        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(
            @RequestParam String email,
            @RequestParam String token,
            Model model
    ) {
        Account account = accountService.confirmEmailProcess(email, token);
        model.addAttribute("nickname", account.getNickname());
        return "account/checked-email";
    }
}
