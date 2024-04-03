package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.application.usacase.AccountMailUsacase;
import com.hobbyboard.domain.account.dto.AccountDto;
import com.hobbyboard.domain.account.dto.signUpForm.SignUpForm;
import com.hobbyboard.domain.account.dto.signUpForm.SignUpFormValidator;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.mapper.AccountMapper;
import com.hobbyboard.domain.account.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Controller
@SessionAttributes("signUpForm")
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;
    private final AccountMailUsacase accountMailUsacase;
    private final AccountMapper accountMapper;

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

    @GetMapping("/check-email/{email}")
    public String emailConfirm(
            @PathVariable String email,
            Model model
    ) {
        model.addAttribute("email", email);
        return "account/check-email";
    }

    @ResponseBody
    @GetMapping("resend-confirm-email")
    public Account resendConfirmEmail(Principal principal) {
        System.out.println(principal);
        return accountMailUsacase.resendConfirmEmail(principal.getName());
    }

    /*
     * @Valid 통한 표준 애너테이션(JSR-303) 검사
     * @InitBinder 통한 SignUpForm 파라미터 받을 때 이메일, 닉네임 유니크 검사
     */
    @PostMapping("/sign-up")
    public String signUpSubmit(
        @Valid SignUpForm signUpForm,
        BindingResult result,
        SessionStatus status,
        HttpServletResponse response,
        HttpServletRequest request
    ) {
        if (result.hasErrors())
            return "account/sign-up";

        AccountDto account = accountMapper.toAccountDto(
                accountMailUsacase.saveSignUpAndSendConfirmEmail(signUpForm));

        accountService.login(account, request, response);

        status.setComplete();

        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(
            @RequestParam String email,
            @RequestParam String token,
            HttpServletResponse response,
            HttpServletRequest request,
            Model model
    ) {
        AccountDto account = accountMapper.
                toAccountDto(accountMailUsacase.confirmEmailProcess(email, token));

        model.addAttribute("nickname", account.getNickname());

        return "account/checked-email";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handle(
            IllegalArgumentException exception,
            Model model
    ) {
        if (exception.getMessage().equals("email"))
            model.addAttribute("error", "wrong.email");
        else
            model.addAttribute("error", "wrong.token");

        return "account/checked-email";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(
            @PathVariable String nickname,
            @CurrentUser AccountDto account,
            Model model
    ) {
        AccountDto byNickname = accountMapper.toAccountDto(
                accountService.findByNickname(nickname));

        if (byNickname == null)
            throw new IllegalArgumentException(nickname + " 에 해당하는 사용자가 없습니다.");

        model.addAttribute("account", byNickname);
        model.addAttribute("isOwner", byNickname.getId().equals(account.getId()));
        return "account/profile";
    }
}
