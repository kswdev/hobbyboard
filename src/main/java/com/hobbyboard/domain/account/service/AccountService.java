package com.hobbyboard.domain.account.service;

import com.hobbyboard.domain.account.dto.AccountDto;
import com.hobbyboard.domain.account.dto.Profile;
import com.hobbyboard.domain.account.dto.nickname.NicknameForm;
import com.hobbyboard.domain.account.dto.notification.Notifications;
import com.hobbyboard.domain.account.dto.passwordForm.PasswordForm;
import com.hobbyboard.domain.account.dto.signUpForm.SignUpForm;
import com.hobbyboard.domain.account.dto.security.UserAccount;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.entity.AccountTag;
import com.hobbyboard.domain.account.repository.AccountRepository;
import com.hobbyboard.domain.account.repository.AccountTagRepository;
import com.hobbyboard.domain.tag.entity.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AccountService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AccountTagRepository accountTagRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final SecurityContextRepository securityContextRepository;

    public Account saveAccount(SignUpForm signUpForm) {

        Account saveAccount = toAccount(signUpForm);
        return accountRepository.save(saveAccount);
    }

    @Transactional(readOnly = true)
    public Optional<Account> findByEmailAndNickname(String emailOrNickname) {
        Account account = accountRepository.findByEmail(emailOrNickname);

        if (account == null)
            account = accountRepository.findByNickname(emailOrNickname);

        if (account == null)
            throw new UsernameNotFoundException(emailOrNickname);

        return Optional.of(account);
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

    public void updateAuthentication(AccountDto account, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(token);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    public void save(Account findAccount) {
        accountRepository.save(findAccount);
    }

    @Transactional(readOnly = true)
    public Account findByNickname(String nickname) {
        return accountRepository.findByNickname(nickname);
    }

    public void completeSignUp(Account findAccount) {
        findAccount.completeSignUp();
    }


    public AccountDto updateProfile(AccountDto accountDto, Profile profile) {

        Account account = accountRepository.findByNickname(accountDto.getNickname());
        modelMapper.map(profile, account);

        return AccountDto.fromAccount(account);
    }

    public AccountDto updatePassword(AccountDto accountDto, PasswordForm passwordForm) {

        Account account = accountRepository.findByNickname(accountDto.getNickname());
        account.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));

        return AccountDto.fromAccount(account);
    }

    public AccountDto updateNotification(AccountDto accountDto, Notifications notifications) {

        Account account = accountRepository.findByNickname(accountDto.getNickname());
        modelMapper.map(notifications, account);

        return AccountDto.fromAccount(account);
    }

    public AccountDto updateNickname(AccountDto accountDto, NicknameForm nicknameForm) {

        Account account = accountRepository.findByNickname(accountDto.getNickname());
        account.setNickname(nicknameForm.getNickname());

        return AccountDto.fromAccount(account);
    }

    public AccountDto findByEmail(String email) {
        return Optional.ofNullable(accountRepository.findByEmail(email))
                .map(AccountDto::fromAccount)
                .orElseThrow(() -> new IllegalArgumentException("email"));

    }

    @Transactional
    public void addTag(AccountDto accountDto, Tag tag) {
        AccountTag accountTag = AccountTag.builder()
                .account(modelMapper.map(accountDto, Account.class))
                .tag(tag)
                .build();

        accountTagRepository.findByAccountIdAndTagId(accountDto.getId(), tag.getId())
                .orElseGet(() -> accountTagRepository.save(accountTag));
    }

    public void removeTag(AccountDto accountDto, Tag tag) {
        accountTagRepository.
                deleteByAccountIdAndTagId(tag.getId(), accountDto.getId());
    }
}
