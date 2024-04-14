package com.hobbyboard.domain.account.entity;


import com.hobbyboard.domain.account.converter.UserRoleConverter;
import com.hobbyboard.domain.account.dto.RoleType;
import com.hobbyboard.domain.study.entity.StudyAccount;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@ToString
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    @Builder.Default
    @Convert(converter = UserRoleConverter.class)
    private Set<RoleType> roleTypes = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<AccountZone> zones = new HashSet<>();

    private boolean emailVerified;
    private LocalDateTime joinedAt;
    private String bio;
    private String url;
    private String occupation;
    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByWeb;
    private boolean studyUpdatedByEmail;

    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<AccountTag> accountTags = new HashSet<>();

    public void completeSignUp() {
        this.setEmailVerified(true);
        this.setJoinedAt(LocalDateTime.now());
        this.roleTypes = Set.of(RoleType.USER);
    }
}
