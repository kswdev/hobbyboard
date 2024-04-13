package com.hobbyboard.domain.zone.repository;

import com.hobbyboard.domain.zone.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCityAndProvince(String city, String province);
}
