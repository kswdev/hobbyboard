package com.hobbyboard.application.controller;

import com.hobbyboard.WithAccount;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.repository.AccountRepository;
import com.hobbyboard.domain.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.hobbyboard.application.controller.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final static String SETTINGS_PROFILE_URL = "/settings/profile";
    private final static String SETTINGS_PASSWORD_URL = "/settings/password";
    private final static String SETTINGS_NOTIFICATION_URL = "/settings/notifications";
    private final static String SETTINGS_ACCOUNT_URL = "/settings/account";

    @BeforeEach
    void before() {
        accountRepository.deleteAll();
        Account account = Account.builder()
                .nickname("nickname")
                .email("email@naver.com")
                .password(passwordEncoder.encode("password"))
                .build();

        accountRepository.save(account);
    }

    @WithAccount
    @DisplayName("계정 닉네임 수정")
    @Test
    void updateNickname_form() throws Exception {
        mockMvc.perform(get(SETTINGS_ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount
    @DisplayName("알림 수정 폼")
    @Test
    void updateNotification_form() throws Exception {
        mockMvc.perform(get(SETTINGS_NOTIFICATION_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notifications"));
    }

    @WithAccount
    @DisplayName("알림 수정 폼")
    @Test
    void updateNotification_form_success() throws Exception {
        mockMvc.perform(post(SETTINGS_NOTIFICATION_URL)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeExists("notifications"));
    }

    @WithAccount
    @DisplayName("패스워드 수정 폼")
    @Test
    void updatePassword_form() throws Exception {

        mockMvc.perform(get(SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount
    @DisplayName("패스워드 수정 폼 - 입력값 정상")
    @Test
    void updatePassword_form_success() throws Exception {

        mockMvc.perform(post(SETTINGS_PASSWORD_URL)
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PASSWORD))
                .andExpect(flash().attributeExists("message"));

        Account byNickname = accountRepository.findByNickname("nickname");
        assertTrue(passwordEncoder.matches("12345678", byNickname.getPassword()));
    }

    @WithAccount()
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개";
        mockMvc.perform(post(SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("nickname");
        assertEquals(bio, account.getBio());
    }

    @WithAccount()
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "짧은 소개가 아닌 매우매우긴~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~너무 긴~~~~~~~~~~~~~~~~~~~~~~수정수정~~~~~~~~~~~~~~~~~~~~~~~~";
        mockMvc.perform(post(SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("nickname");
        assertNull(account.getBio());
    }
}