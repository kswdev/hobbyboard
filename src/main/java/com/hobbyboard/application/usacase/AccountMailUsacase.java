package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.signUpForm.SignUpForm;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.service.AccountService;
import com.hobbyboard.domain.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountMailUsacase {

    private final MailService mailService;
    private final AccountService accountService;

    public Account saveSignUpAndSendConfirmEmail(SignUpForm signUpForm) {
        Account saveAccount = accountService.saveAccount(signUpForm);
        mailService.sendSignUpConfirmEmail(saveAccount);

        return saveAccount;
    }
    public Account resendConfirmEmail(String emailId) {
        Account account = accountService.findAccountAndGenerateCheckToken(emailId);
        mailService.sendSignUpConfirmEmail(account);
        return account;
    }
}
