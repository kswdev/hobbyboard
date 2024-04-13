package com.hobbyboard.domain.account.entity;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.zone.entity.Zone;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class AccountZone {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Zone zone;
}
