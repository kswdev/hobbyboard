package com.hobbyboard.domain.account.repository;

import com.hobbyboard.domain.account.entity.AccountZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccountZoneRepository extends JpaRepository<AccountZone, Long> {

    @Modifying
    @Query("delete from AccountZone az where az.account.id = :accountId and az.zone.id = :zoneId")
    void deleteByAccountIdAndZoneId(Long accountId, Long zoneId);
}
