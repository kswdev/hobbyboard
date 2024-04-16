package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.entity.AccountZone;
import com.hobbyboard.domain.account.service.AccountReadService;
import com.hobbyboard.domain.account.service.AccountWriteService;
import com.hobbyboard.domain.zone.dto.request.ZoneForm;
import com.hobbyboard.domain.zone.entity.Zone;
import com.hobbyboard.domain.zone.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Component
public class AccountZoneUsacase {

    private final ZoneService zoneService;
    private final AccountWriteService accountWriteService;
    private final AccountReadService accountReadService;

    @Transactional
    public void addZone(AccountDto accountDto, ZoneForm zoneForm) {
        Account account = accountWriteService.findById(accountDto.getId());
        Zone zone = zoneService.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        AccountZone accountZone = AccountZone.builder()
                .account(account)
                .zone(zone)
                .build();

        account.getZones().add(accountZone);
    }

    @Transactional
    public void removeZone(AccountDto accountDto, ZoneForm zoneForm) {
        Account account = accountWriteService.findById(accountDto.getId());
        Zone zone = zoneService.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        AccountZone accountZone = accountReadService.findByAccountIdAndZoneId(account.getId(), zone.getId());

        if (!ObjectUtils.isEmpty(accountZone))
            account.getZones().remove(accountZone);
    }
}
