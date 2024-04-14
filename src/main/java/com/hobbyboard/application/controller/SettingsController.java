package com.hobbyboard.application.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.application.usacase.AccountTagUsacase;
import com.hobbyboard.application.usacase.AccountZoneUsacase;
import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.dto.Profile;
import com.hobbyboard.domain.account.dto.nickname.NicknameForm;
import com.hobbyboard.domain.account.dto.nickname.NicknameFormValidator;
import com.hobbyboard.domain.account.dto.notification.Notifications;
import com.hobbyboard.domain.account.dto.passwordForm.PasswordForm;
import com.hobbyboard.domain.account.dto.passwordForm.PasswordFormValidator;
import com.hobbyboard.domain.account.entity.AccountZone;
import com.hobbyboard.domain.account.service.AccountService;
import com.hobbyboard.domain.tag.dto.TagForm;
import com.hobbyboard.domain.tag.entity.Tag;
import com.hobbyboard.domain.tag.repository.TagRepository;
import com.hobbyboard.domain.tag.service.TagService;
import com.hobbyboard.domain.zone.dto.ZoneDto;
import com.hobbyboard.domain.zone.dto.request.ZoneForm;
import com.hobbyboard.domain.zone.entity.Zone;
import com.hobbyboard.domain.zone.service.ZoneService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hobbyboard.application.controller.SettingsController.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(ROOT + SETTINGS)
@Controller
public class SettingsController {

    private final PasswordFormValidator passwordFormValidator;
    private final NicknameFormValidator nicknameFormValidator;
    private final AccountService accountService;
    private final ZoneService zoneService;
    private final AccountZoneUsacase accountZoneUsacase;
    private final AccountTagUsacase accountTagUsacase;
    private final TagService tagService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    static final String ROOT = "/";
    static final String SETTINGS = "settings";
    static final String PROFILE = "/profile";
    static final String PASSWORD = "/password";
    static final String NOTIFICATIONS = "/notifications";
    static final String ACCOUNT = "/account";
    static final String TAGS = "/tags";
    static final String ZONES = "/zones";

    @InitBinder("passwordForm")
    public void passwordBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @InitBinder("nicknameForm")
    public void nicknameBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @GetMapping(ZONES)
    public String updateZones(
            @CurrentUser AccountDto accountDto,
            Model model
    ) throws JsonProcessingException {

        List<String> zoneList = zoneService.findAll().stream()
                .map(Zone::toString)
                .toList();

        List<String> zones = accountService.getZones(accountDto);

        model.addAttribute("zones", zones);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(zoneList));
        return SETTINGS + ZONES;
    }

    @PostMapping(ZONES + "/add")
    @ResponseBody
    public ResponseEntity<Void> addZones(
            @CurrentUser AccountDto accountDto,
            @RequestBody ZoneForm zoneForm
    ) {
        accountZoneUsacase.addZone(accountDto, zoneForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ZONES + "/remove")
    @ResponseBody
    public ResponseEntity<Void> removeZones(
            @CurrentUser AccountDto accountDto,
            @RequestBody ZoneForm zoneForm
    ) {
        accountZoneUsacase.removeZone(accountDto, zoneForm);
        return ResponseEntity.ok().build();
    }

    @GetMapping(TAGS)
    public String updateTags(
            @CurrentUser AccountDto accountDto,
            Model model
    ) throws JsonProcessingException {

        Set<String> tags = accountService.getTags(accountDto);
        Set<String> allTags = tagService.findAll().stream()
                .map(Tag::getTitle)
                .collect(Collectors.toUnmodifiableSet());

        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        model.addAttribute("tags", tags);
        model.addAttribute("account", accountDto);
        return SETTINGS + TAGS;
    }

    @PostMapping(TAGS + "/add")
    @ResponseBody
    public ResponseEntity<Void> addTag(
            @CurrentUser AccountDto accountDto,
            @RequestBody TagForm tagForm
    ) {
        accountTagUsacase.addTag(accountDto, tagForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping(TAGS + "/remove")
    @ResponseBody
    public ResponseEntity<Void> removeTag(
            @CurrentUser AccountDto accountDto,
            @RequestBody TagForm tagForm
    ) {
        accountTagUsacase.remove(accountDto, tagForm);
        return ResponseEntity.ok().build();
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
