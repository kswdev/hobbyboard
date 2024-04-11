package com.hobbyboard.domain.account.entity;

import com.hobbyboard.domain.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "account_tag")
@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccountTag {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Tag tag;
}
