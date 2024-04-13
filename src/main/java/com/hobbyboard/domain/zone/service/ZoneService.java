package com.hobbyboard.domain.zone.service;

import com.hobbyboard.domain.zone.entity.Zone;
import com.hobbyboard.domain.zone.repository.ZoneRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static java.nio.charset.StandardCharsets.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @Transactional
    @PostConstruct
    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zone_kr.csv");
            List<Zone> zones = Files.readAllLines(resource.getFile().toPath(), UTF_8).stream()
                    .map(line -> {
                       String[] split = line.split(",");
                       return Zone.builder()
                               .city(split[0])
                               .localNameOfCity(split[1])
                               .province(split[2])
                               .build();
                    }).toList();

            zoneRepository.saveAll(zones);
        }
    }

    public List<Zone> findAll() {
        return zoneRepository.findAll();
    }

    public Zone findByCityAndProvince(String cityName, String provinceName) {
        return zoneRepository.findByCityAndProvince(cityName, provinceName);
    }
}


