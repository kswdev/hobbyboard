package com.hobbyboard.domain.account.dto.account;

import com.hobbyboard.domain.account.entity.AccountTag;
import com.hobbyboard.domain.tag.dto.TagDto;
import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccountTagDto implements Serializable {

    private Long id;

    private AccountDto accountDto;

    private TagDto tag;

    public static AccountTagDto from (AccountTag accountTag) {
        return AccountTagDto.builder()
                .id(accountTag.getId())
                .accountDto(AccountDto.from(accountTag.getAccount()))
                .tag(TagDto.from(accountTag.getTag()))
                .build();
    }
}
