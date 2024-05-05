package com.hobbyboard.domain.account;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.service.AccountReadService;
import com.hobbyboard.domain.account.service.AccountWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired AccountReadService accountReadService;

    public Account getAccount(String nickname) {

        return accountReadService.findByNickname(nickname);
    }
}
