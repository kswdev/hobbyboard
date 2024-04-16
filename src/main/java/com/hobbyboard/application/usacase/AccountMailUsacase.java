package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.dto.signUpForm.SignUpForm;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.service.AccountReadService;
import com.hobbyboard.domain.account.service.AccountWriteService;
import com.hobbyboard.domain.mail.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountMailUsacase {

    private final MailService mailService;
    private final AccountWriteService accountWriteService;
    private final AccountReadService accountReadService;

    public void sendPasswordChangeEmail(String email) {
        mailService.sendPasswordChangeEmail(email);
    }

    public Account saveSignUpAndSendConfirmEmail(SignUpForm signUpForm) {
        Account saveAccount = accountWriteService.saveAccount(signUpForm);
        mailService.sendSignUpConfirmEmail(saveAccount);

        return saveAccount;
    }
    public Account resendConfirmEmail(String nickname) {
        Account account = accountReadService.findByNickname(nickname);

        if (account == null)
            throw new IllegalArgumentException(nickname + "은 없는 닉네임입니다.");

        mailService.sendSignUpConfirmEmail(account);

        return account;
    }

    public Account confirmEmailProcess(String email, String token) {

        Account findAccount = accountReadService.findByEmailAndNickname(email)
                .orElseThrow(() -> new IllegalArgumentException("email"));

        if (mailService.isValidToken(findAccount.getEmail(), token)) {
            accountWriteService.completeSignUp(findAccount);
            accountWriteService.save(findAccount);
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

        AccountDto findAccount = accountReadService.findByEmail(email);

        if (mailService.isValidToken(email, token))
            accountWriteService.updateAuthentication(findAccount, request, response);
        else
            throw new IllegalArgumentException("token");
    }
}
