package com.hobbyboard.domain.study.dto;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.dto.security.UserAccount;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyTag;
import com.hobbyboard.domain.study.entity.StudyZone;
import com.hobbyboard.domain.tag.entity.Tag;
import com.hobbyboard.domain.zone.dto.ZoneDto;
import lombok.*;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ToString
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class StudyDto implements Serializable {

    private Long id;
    @Builder.Default
    private Set<AccountDto> managers = new HashSet<>();
    @Builder.Default
    private Set<AccountDto> members = new HashSet<>();
    @Builder.Default
    private Set<String> tags = new HashSet<>();
    @Builder.Default
    private Set<ZoneDto> zones = new HashSet<>();
    private String path;
    private String title;
    private String shortDescription;
    private String fullDescription;
    private String image;
    private LocalDateTime publishedDateTime;
    private LocalDateTime closedDateTime;
    private LocalDateTime recruitingUpdatedDateTime;
    private boolean recruiting;
    private boolean published;
    private boolean closed;
    private boolean useBanner;
    private int memberCount;

    public boolean isRemovable() {
        return !published;
    }

    public static StudyDto fromWithAll(Study study) {
        return StudyDto.builder()
                .id(study.getId())
                .tags(study.getTags().stream()
                        .map(StudyTag::getTag)
                        .map(Tag::getTitle)
                        .collect(Collectors.toUnmodifiableSet()))
                .zones(study.getZones().stream()
                        .map(StudyZone::getZone)
                        .map(ZoneDto::from)
                        .collect(Collectors.toUnmodifiableSet()))
                .managers(study.getManagers().stream()
                        .map(AccountDto::from)
                        .collect(Collectors.toUnmodifiableSet()))
                .members(study.getMembers().stream()
                        .map(AccountDto::from)
                        .collect(Collectors.toUnmodifiableSet()))
                .path(study.getPath())
                .title(study.getTitle())
                .shortDescription(study.getShortDescription())
                .fullDescription(study.getFullDescription())
                .image(study.getImage())
                .publishedDateTime(study.getPublishedDateTime())
                .closedDateTime(study.getClosedDateTime())
                .recruitingUpdatedDateTime(study.getRecruitingUpdatedDateTime())
                .recruiting(study.isRecruiting())
                .published(study.isPublished())
                .closed(study.isClosed())
                .useBanner(study.isUseBanner())
                .memberCount(study.getMembers().size())
                .build();
    }

    public static StudyDto from(Study study) {
        return StudyDto.builder()
                .id(study.getId())
                .path(study.getPath())
                .title(study.getTitle())
                .shortDescription(study.getShortDescription())
                .fullDescription(study.getFullDescription())
                .image(study.getImage())
                .publishedDateTime(study.getPublishedDateTime())
                .closedDateTime(study.getClosedDateTime())
                .recruitingUpdatedDateTime(study.getRecruitingUpdatedDateTime())
                .recruiting(study.isRecruiting())
                .published(study.isPublished())
                .closed(study.isClosed())
                .useBanner(study.isUseBanner())
                .memberCount(study.getMembers().size())
                .build();
    }

    public boolean isJoinable(UserAccount userAccount) {
        return this.isPublished() && this.isRecruiting()
                && !members.contains(userAccount.getAccount())
                && !managers.contains(userAccount.getAccount());
    }

    public boolean isMember(UserAccount userAccount) {
        return members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return managers.contains(userAccount.getAccount());
    }

    public boolean isMemberOf(AccountDto accountDto) {
        return members.contains(accountDto);
    }

    public boolean isManagerOf(AccountDto accountDto) {
        return managers.contains(accountDto);
    }

    public String getEncodePath() {
        return URLEncoder.encode(this.getPath(), StandardCharsets.UTF_8);
    }
}
