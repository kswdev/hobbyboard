package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.dto.signUpForm.SignUpForm;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.service.AccountService;
import com.hobbyboard.domain.mail.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountMailUsacase {

    private final MailService mailService;
    private final AccountService accountService;

    public void sendPasswordChangeEmail(String email) {
        mailService.sendPasswordChangeEmail(email);
    }

    public Account saveSignUpAndSendConfirmEmail(SignUpForm signUpForm) {
        Account saveAccount = accountService.saveAccount(signUpForm);
        mailService.sendSignUpConfirmEmail(saveAccount);

        return saveAccount;
    }
    public Account resendConfirmEmail(String nickname) {
        Account account = accountService.findByNickname(nickname);

        if (account == null)
            throw new IllegalArgumentException(nickname + "은 없는 닉네임입니다.");

        mailService.sendSignUpConfirmEmail(account);

        return account;
    }

    public Account confirmEmailProcess(String email, String token) {

        Account findAccount = accountService.findByEmailAndNickname(email)
                .orElseThrow(() -> new IllegalArgumentException("email"));

        if (mailService.isValidToken(findAccount.getEmail(), token)) {
            accountService.completeSignUp(findAccount);
            accountService.save(findAccount);
        } else
            throw new IllegalArgumentException("token");

        return findAccount;
    }

    public void confirmLoginByEmailProcess(
            String email,
            String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        AccountDto findAccount = accountService.findByEmail(email);

        if (mailService.isValidToken(email, token))
            accountService.updateAuthentication(findAccount, request, response);
        else
            throw new IllegalArgumentException("token");
    }
}
