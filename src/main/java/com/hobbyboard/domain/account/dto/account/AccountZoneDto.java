package com.hobbyboard.domain.account.dto.account;

import com.hobbyboard.domain.account.entity.AccountZone;
import com.hobbyboard.domain.zone.dto.ZoneDto;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccountZoneDto {

    private Long id;
    private AccountDto accountDto;
    private ZoneDto zoneDto;

    public static AccountZoneDto from (AccountZone accountZone) {
        return AccountZoneDto.builder()
                .id(accountZone.getId())
                .accountDto(AccountDto.from(accountZone.getAccount()))
                .zoneDto(ZoneDto.from(accountZone.getZone()))
                .build();
    }
}
