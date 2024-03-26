package com.hobbyboard.domain.account.service;

import com.hobbyboard.domain.account.dto.SignUpForm;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    @Transactional
    public void processNewAccount(SignUpForm signUpForm) {
        Account saveAccount = saveAccount(signUpForm);
        saveAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(saveAccount);
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

        if (findAccount.getEmailCheckToken().equals(token)) {
            findAccount.setEmailVerified(true);
            findAccount.setJoinedAt(LocalDateTime.now());
        } else
            throw new IllegalArgumentException("token");

        return findAccount;
    }
}
