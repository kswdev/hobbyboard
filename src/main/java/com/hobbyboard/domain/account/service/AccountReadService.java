package com.hobbyboard.domain.account.service;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.entity.AccountTag;
import com.hobbyboard.domain.account.entity.AccountZone;
import com.hobbyboard.domain.account.repository.AccountRepository;
import com.hobbyboard.domain.account.repository.AccountTagRepository;
import com.hobbyboard.domain.account.repository.AccountZoneRepository;
import com.hobbyboard.domain.tag.entity.Tag;
import com.hobbyboard.domain.zone.entity.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountReadService {

    private final AccountRepository accountRepository;
    private final AccountTagRepository accountTagRepository;
    private final AccountZoneRepository accountZoneRepository;

    public Optional<Account> findByEmailAndNickname(String emailOrNickname) {
        Account account = accountRepository.findByEmail(emailOrNickname);

        if (account == null)
            account = accountRepository.findByNickname(emailOrNickname);

        if (account == null)
            throw new UsernameNotFoundException(emailOrNickname);

        return Optional.of(account);
    }

    public Account findByNickname(String nickname) {
        return accountRepository.findByNickname(nickname);
    }

    public AccountDto findByEmail(String email) {
        return Optional.ofNullable(accountRepository.findByEmail(email))
                .map(AccountDto::fromAccount)
                .orElseThrow(() -> new IllegalArgumentException("email"));
    }

    public Set<String> getTags(AccountDto accountDto) {
        return accountTagRepository.findByAccountId(accountDto.getId()).stream()
                .map(AccountTag::getTag)
                .map(Tag::getTitle)
                .collect(Collectors.toUnmodifiableSet());
    }

    public List<String> getZones(AccountDto accountDto) {
        return accountRepository.findById(accountDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("없는 ID"))
                .getZones().stream()
                .map(AccountZone::getZone)
                .map(Zone::toString)
                .toList();
    }

    public AccountTag findByAccountIdAndTagId(Long accountId, Long tagId) {
        return accountTagRepository.findByAccountIdAndTagId(accountId, tagId);
    }

    public AccountZone findByAccountIdAndZoneId(Long accountId, Long zoneId) {
        return accountZoneRepository.findByAccountIdAndZoneId(accountId, zoneId);
    }
}
