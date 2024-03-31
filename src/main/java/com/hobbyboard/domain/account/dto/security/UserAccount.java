package com.hobbyboard.domain.account.dto.security;

import com.hobbyboard.domain.account.dto.AccountDto;
import com.hobbyboard.domain.account.dto.RoleType;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.stream.Collectors;

@Getter
public class UserAccount extends User {

    private final AccountDto account;

    public UserAccount(AccountDto account) {
        super(account.getEmail(), account.getPassword(), account
                .getRoleTypes().stream()
                .map(RoleType::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet()));

        this.account = account;
    }
}
