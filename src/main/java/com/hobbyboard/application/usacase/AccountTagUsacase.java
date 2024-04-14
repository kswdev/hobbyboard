package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.entity.AccountTag;
import com.hobbyboard.domain.account.service.AccountService;
import com.hobbyboard.domain.tag.dto.TagForm;
import com.hobbyboard.domain.tag.entity.Tag;
import com.hobbyboard.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AccountTagUsacase {

    private final TagService tagService;
    private final AccountService accountService;


    @Transactional
    public void addTag(AccountDto accountDto, TagForm tagForm) {
        Account account = accountService.findById(accountDto.getId());
        Tag tag = tagService.findByTitle(tagForm.getTagTitle()).orElseGet(() ->
                  tagService.save(new Tag(tagForm.getTagTitle())));


        AccountTag accountTag = AccountTag.builder()
                .account(account)
                .tag(tag)
                .build();

        account.getAccountTags().add(accountTag);
    }

    @Transactional
    public void remove(AccountDto accountDto, TagForm tagForm) {
        Account account = accountService.findById(accountDto.getId());
        Tag tag = tagService.findByTitle(tagForm.getTagTitle()).get();

        AccountTag accountTag = accountService.findByAccountIdAndTagId(account.getId(), tag.getId());
        if (!ObjectUtils.isEmpty(accountTag)) {
            account.getAccountTags().remove(accountTag);
        }
    }
}
