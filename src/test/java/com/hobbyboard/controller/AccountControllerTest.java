package com.hobbyboard.controller;

import com.hobbyboard.application.usacase.AccountMailUsacase;
import com.hobbyboard.domain.account.dto.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.mapper.AccountMapper;
import com.hobbyboard.domain.account.repository.AccountRepository;
import com.hobbyboard.domain.mail.service.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    AccountMapper accountMapper;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountMailUsacase accountMailUsacase;
    @MockBean
    JavaMailSender javaMailSender;
    @Autowired
    MailService mailService;

    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {

        mockMvc.perform(get("/check-email-token")
                .param("token", "sdfsdfdd")
                .param("email", "email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"));
    }

    @Transactional
    @Test
    void checkEmailToken_with_right_input() throws Exception {

        Account account = Account.builder()
                            .email("test@email.com")
                            .password("12345678")
                            .nickname("sunwon")
                            .build();

        accountRepository.save(account);

        String token  = mailService.setEmailCheckToken(account);

        AccountDto newAccount =  accountMapper.
                toAccountDto(accountMailUsacase.confirmEmailProcess(account.getEmail(), token));

        mockMvc.perform(get("/check-email-token")
                        .param("token", token)
                        .param("email", account.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(view().name("account/checked-email"));
    }

    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    void signUpSubmit_with_right_input() throws Exception {

        mockMvc.perform(post("/sign-up")
                        .param("nickname", "ksw96")
                        .param("email", "ksw96@gmail.com")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail("ksw96@gmail.com");

        assertNotNull(account);
        assertNotEquals(account.getPassword(), "12345678");

        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @DisplayName("회원 가입 화면 - 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {

        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {

        mockMvc.perform(post("/sign-up")
            .param("nickname", "ksw96")
            .param("email", "email...")
            .param("password", "13424")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("account/sign-up"))
            .andExpect(unauthenticated());

    }
}