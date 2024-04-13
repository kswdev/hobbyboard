package com.hobbyboard.domain.zone.dto.request;

import com.hobbyboard.domain.zone.entity.Zone;
import lombok.*;

import java.io.Serializable;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ZoneForm implements Serializable {

    private String zoneName;

    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getLocalNameOfCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

    public Zone getZone() {
        return Zone.builder()
                .city(this.getCityName())
                .localNameOfCity(this.getLocalNameOfCity())
                .province(this.getProvinceName())
                .build();
    }
}
