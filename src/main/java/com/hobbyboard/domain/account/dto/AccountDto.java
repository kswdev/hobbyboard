package com.hobbyboard.domain.account.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
public class AccountDto implements Serializable {

    private Long id;

    private String email;

    private String nickname;

    private String password;

    private Set<RoleType> roleTypes = new HashSet<>();

    private boolean emailVerified;
    private LocalDateTime joinedAt;
    private String bio;
    private String url;
    private String occupation;
    private String location;

    private String profileImage;

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByWeb;
    private boolean studyUpdatedResultByEmail;

    @Builder
    public AccountDto(Long id, String email, String nickname, String password, Set<RoleType> roleTypes, boolean emailVerified, LocalDateTime joinedAt, String bio, String url, String occupation, String location, String profileImage, boolean studyCreatedByEmail, boolean studyCreatedByWeb, boolean studyEnrollmentResultByEmail, boolean studyEnrollmentResultByWeb, boolean studyUpdatedByWeb, boolean studyUpdatedResultByEmail) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.roleTypes = roleTypes;
        this.emailVerified = emailVerified;
        this.joinedAt = joinedAt;
        this.bio = bio;
        this.url = url;
        this.occupation = occupation;
        this.location = location;
        this.profileImage = profileImage;
        this.studyCreatedByEmail = studyCreatedByEmail;
        this.studyCreatedByWeb = studyCreatedByWeb;
        this.studyEnrollmentResultByEmail = studyEnrollmentResultByEmail;
        this.studyEnrollmentResultByWeb = studyEnrollmentResultByWeb;
        this.studyUpdatedByWeb = studyUpdatedByWeb;
        this.studyUpdatedResultByEmail = studyUpdatedResultByEmail;
    }
}
