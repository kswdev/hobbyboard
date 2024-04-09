package com.hobbyboard.domain.account.entity;


import com.hobbyboard.domain.account.converter.UserRoleConverter;
import com.hobbyboard.domain.account.dto.Profile;
import com.hobbyboard.domain.account.dto.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

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
    private boolean studyUpdatedResultByEmail;

    public void completeSignUp() {
        this.setEmailVerified(true);
        this.setJoinedAt(LocalDateTime.now());
        this.roleTypes = Set.of(RoleType.USER);
    }

    public void updateProfile(Profile profile) {
        this.setUrl(profile.getUrl());
        this.setOccupation(profile.getOccupation());
        this.setLocation(profile.getLocation());
        this.setBio(profile.getBio());
        this.setProfileImage(profile.getProfileImage());
    }
}
