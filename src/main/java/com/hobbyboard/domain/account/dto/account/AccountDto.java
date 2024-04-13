package com.hobbyboard.domain.account.dto.account;

import com.hobbyboard.domain.account.dto.RoleType;
import com.hobbyboard.domain.account.entity.Account;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @Builder
public class AccountDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098267757690L;

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
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

    public static AccountDto fromAccount(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .password(account.getPassword())
                .email(account.getEmail())
                .nickname(account.getNickname())
                .roleTypes(account.getRoleTypes())
                .emailVerified(account.isEmailVerified())
                .joinedAt(account.getJoinedAt())
                .bio(account.getBio())
                .url(account.getUrl())
                .occupation(account.getOccupation())
                .location(account.getLocation())
                .profileImage(account.getProfileImage())
                .studyCreatedByEmail(account.isStudyCreatedByEmail())
                .studyCreatedByWeb(account.isStudyCreatedByWeb())
                .studyEnrollmentResultByEmail(account.isStudyEnrollmentResultByEmail())
                .studyEnrollmentResultByWeb(account.isStudyEnrollmentResultByWeb())
                .studyUpdatedByWeb(account.isStudyUpdatedByWeb())
                .studyUpdatedByEmail(account.isStudyUpdatedByEmail())
                .build();
    }
}
