package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.domain.account.dto.AccountDto;
import com.hobbyboard.domain.account.dto.Profile;
import com.hobbyboard.domain.account.dto.passwordForm.PasswordForm;
import com.hobbyboard.domain.account.dto.passwordForm.PasswordFormValidator;
import com.hobbyboard.domain.account.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class SettingsController {

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    private final PasswordFormValidator passwordFormValidator;
    private final AccountService accountService;
    public final static String SETTINGS_PROFILE_VIEW_NAME = "/settings/profile";
    public final static String SETTINGS_PROFILE_URL = "/settings/profile";
    public final static String SETTINGS_PASSWORD_VIEW_NAME = "/settings/password";
    public final static String SETTINGS_PASSWORD_URL = "/settings/password";

    @GetMapping(SETTINGS_PROFILE_URL)
    public String updateProfileForm(
            @CurrentUser AccountDto accountDto,
            Model model
    ) {
        model.addAttribute("account", accountDto);
        model.addAttribute("profile", Profile.fromAccountDto(accountDto));

        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(
            @CurrentUser AccountDto accountDto,
            @Valid Profile profile,
            BindingResult errors,
            RedirectAttributes attributes,
            HttpServletResponse response,
            HttpServletRequest request,
            Model model
    ) {
        if (errors.hasErrors()) {
            model.addAttribute("account", accountDto);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        AccountDto updatedAccountDto = accountService.updateProfile(accountDto, profile);

        accountService.login(updatedAccountDto, request, response);

        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:" + SETTINGS_PROFILE_VIEW_NAME;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String updatePasswordForm(
            @CurrentUser AccountDto accountDto,
            Model model
    ) {
        model.addAttribute("account", accountDto);
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(
            @CurrentUser AccountDto accountDto,
            @Valid PasswordForm passwordForm,
            BindingResult errors,
            RedirectAttributes attributes,
            HttpServletResponse response,
            HttpServletRequest request,
            Model model
    ) {
        if (errors.hasErrors()) {
            model.addAttribute(passwordForm);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        AccountDto updatePassword = accountService.updatePassword(accountDto, passwordForm);
        accountService.login(updatePassword, request, response);

        attributes.addFlashAttribute("message", "비밀번호를 수정했습니다.");
        return "redirect:" + SETTINGS_PASSWORD_VIEW_NAME;
    }
}
