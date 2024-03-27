package com.hobbyboard.domain.account.service;

import com.hobbyboard.domain.account.dto.SignUpForm;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final SecurityContextRepository securityContextRepository;

    @Transactional
    public Account processNewAccount(SignUpForm signUpForm) {

        Account saveAccount = saveAccount(signUpForm);
        saveAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(saveAccount);

        return saveAccount;
    }

    private Account saveAccount(SignUpForm signUpForm) {

        Account newAccount = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();

        return accountRepository.save(newAccount);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                                              "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public Account confirmEmailProcess(String email, String token) {
        Account findAccount = accountRepository.findByEmail(email);

        if (findAccount == null)
            throw new IllegalArgumentException("email");

        if (findAccount.isValidToken(token))
            findAccount.completeSignUp();
        else
            throw new IllegalArgumentException("token");

        return findAccount;
    }

    public void login(Account account, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                account.getNickname(),
                account.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(token);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}
