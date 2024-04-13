package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.entity.AccountZone;
import com.hobbyboard.domain.account.service.AccountService;
import com.hobbyboard.domain.zone.dto.request.ZoneForm;
import com.hobbyboard.domain.zone.entity.Zone;
import com.hobbyboard.domain.zone.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class AccountZoneUsacase {

    private final ZoneService zoneService;
    private final AccountService accountService;

    @Transactional
    public void addZone(AccountDto accountDto, ZoneForm zoneForm) {
        Account account = accountService.findById(accountDto.getId());
        Zone zone = zoneService.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        AccountZone accountZone = AccountZone.builder()
                .account(account)
                .zone(zone)
                .build();

        account.getZones().add(accountZone);
    }

    public void removeZone(AccountDto accountDto, ZoneForm zoneForm) {
        Account account = accountService.findById(accountDto.getId());
        Zone zone = zoneService.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        accountService.deleteByAccountIdAndZoneId(account.getId(), zone.getId());
    }
}
