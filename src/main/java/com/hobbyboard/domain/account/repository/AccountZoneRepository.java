package com.hobbyboard.domain.account.repository;

import com.hobbyboard.domain.account.entity.AccountZone;
import com.hobbyboard.domain.zone.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface AccountZoneRepository extends JpaRepository<AccountZone, Long> {
    AccountZone findByAccountIdAndZoneId(Long accountId, Long zoneId);

    @Modifying
    @Query("delete from AccountZone az where az.account.id = :accountId and az.zone.id = :zoneId")
    void deleteByAccountIdAndZoneId(Long accountId, Long zoneId);

    @Query("select az from AccountZone az where az.zone in (:zones)")
    Set<AccountZone> findByZones(Set<Zone> zones);
}
