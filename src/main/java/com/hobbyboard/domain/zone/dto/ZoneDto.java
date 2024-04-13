package com.hobbyboard.domain.zone.dto;

import com.hobbyboard.domain.zone.entity.Zone;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ZoneDto implements Serializable {

    private Long id;
    private String city;
    private String localNameOfCity;
    private String province;

    @Override
    public String toString() {
        return getCity() + "(" + this.getLocalNameOfCity() + ")" + "/" + this.getProvince();
    };

    public static ZoneDto from (Zone zone) {
        return new ZoneDto(zone.getId(), zone.getCity(), zone.getLocalNameOfCity(), zone.getProvince());
    }
}