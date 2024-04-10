package com.hobbyboard.application.controller;

import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.domain.account.dto.AccountDto;
import com.hobbyboard.domain.account.dto.Profile;
import com.hobbyboard.domain.account.dto.nickname.NicknameForm;
import com.hobbyboard.domain.account.dto.nickname.NicknameFormValidator;
import com.hobbyboard.domain.account.dto.notification.Notifications;
import com.hobbyboard.domain.account.dto.passwordForm.PasswordForm;
import com.hobbyboard.domain.account.dto.passwordForm.PasswordFormValidator;
import com.hobbyboard.domain.account.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.hobbyboard.application.controller.SettingsController.*;

@RequiredArgsConstructor
@RequestMapping(ROOT + SETTINGS)
@Controller
public class SettingsController {

    private final PasswordFormValidator passwordFormValidator;
    private final NicknameFormValidator nicknameFormValidator;
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    static final String ROOT = "/";
    static final String SETTINGS = "settings";
    static final String PROFILE = "/profile";
    static final String PASSWORD = "/password";
    static final String NOTIFICATIONS = "/notifications";
    static final String ACCOUNT = "/account";

    @InitBinder("passwordForm")
    public void passwordBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @InitBinder("nicknameForm")
    public void nicknameBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameFormValidator);
    }


    @GetMapping(ACCOUNT)
    public String updateAccountForm(
            @CurrentUser AccountDto accountDto,
            Model model
    ) {
        model.addAttribute("account", accountDto);
        model.addAttribute(new NicknameForm(accountDto.getNickname()));
        return SETTINGS + ACCOUNT;
    }

    @PostMapping(ACCOUNT)
    public String updateAccount(
            @CurrentUser AccountDto accountDto,
            @Valid NicknameForm nicknameForm,
            BindingResult errors,
            RedirectAttributes attributes,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        if (errors.hasErrors()) {
            model.addAttribute(nicknameForm);
            return SETTINGS + ACCOUNT;
        }

        AccountDto account = accountService.updateNickname(accountDto, nicknameForm);
        accountService.updateAuthentication(account, request, response);

        attributes.addFlashAttribute("message", "닉네임시 수정되었습니다.");

        return "redirect:/" + SETTINGS + ACCOUNT;
    }

    @GetMapping(NOTIFICATIONS)
    public String updateNotificationForm(
            @CurrentUser AccountDto accountDto,
            Model model
    ) {
        model.addAttribute("account", accountDto);
        model.addAttribute(modelMapper.map(accountDto, Notifications.class));
        return SETTINGS + NOTIFICATIONS;
    }

    @PostMapping(NOTIFICATIONS)
    public String updateNotification(
            @CurrentUser AccountDto accountDto,
            Notifications notifications,
            RedirectAttributes attributes,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        AccountDto updatedAccount = accountService.updateNotification(accountDto, notifications);
        accountService.updateAuthentication(updatedAccount, request, response);
        attributes.addFlashAttribute(notifications);
        attributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:/" + SETTINGS + NOTIFICATIONS;
    }
    @GetMapping(PROFILE)
    public String updateProfileForm(
            @CurrentUser AccountDto accountDto,
            Model model
    ) {
        model.addAttribute("account", accountDto);
        model.addAttribute("profile", Profile.fromAccountDto(accountDto));

        return SETTINGS + PROFILE;
    }

    @PostMapping(PROFILE)
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
            return SETTINGS + PROFILE;
        }

        AccountDto updatedAccountDto = accountService.updateProfile(accountDto, profile);

        accountService.updateAuthentication(updatedAccountDto, request, response);

        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:/" + SETTINGS + PROFILE;
    }

    @GetMapping(PASSWORD)
    public String updatePasswordForm(
            @CurrentUser AccountDto accountDto,
            Model model
    ) {
        model.addAttribute("account", accountDto);
        model.addAttribute(new PasswordForm());
        return SETTINGS + PASSWORD;
    }

    @PostMapping(PASSWORD)
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
            return SETTINGS + PASSWORD;
        }

        AccountDto updatePassword = accountService.updatePassword(accountDto, passwordForm);
        accountService.updateAuthentication(updatePassword, request, response);

        attributes.addFlashAttribute("message", "비밀번호를 수정했습니다.");
        return "redirect:/" + SETTINGS + PASSWORD;
    }
}
