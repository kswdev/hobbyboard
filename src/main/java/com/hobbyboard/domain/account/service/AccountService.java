package com.hobbyboard.domain.account.service;

import com.hobbyboard.domain.account.dto.AccountDto;
import com.hobbyboard.domain.account.dto.signUpForm.SignUpForm;
import com.hobbyboard.domain.account.dto.security.UserAccount;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final SecurityContextRepository securityContextRepository;

    @Transactional
    public Account saveAccount(SignUpForm signUpForm) {

        Account saveAccount = toAccount(signUpForm);
        saveAccount.generateEmailCheckToken();
        return accountRepository.save(saveAccount);
    }

    public Optional<Account> findByEmail(String email) {
        return Optional.ofNullable(accountRepository.findByEmail(email));
    }

    private Account toAccount(SignUpForm signUpForm) {
        return Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
    }


    @Transactional
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

    public void login(AccountDto account, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(token);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    @Transactional
    public Account findAccountAndGenerateCheckToken(String emailId) {
        Account account = accountRepository.findByEmail(emailId);
        account.generateEmailCheckToken();
        return account;
    }
}
