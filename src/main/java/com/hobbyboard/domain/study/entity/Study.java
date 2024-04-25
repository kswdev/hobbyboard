package com.hobbyboard.domain.study.entity;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("studyAccounts"),
})
@NamedEntityGraph(name = "Study.withTagAndManager", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("studyAccounts"),
})
@NamedEntityGraph(name = "Study.withZoneAndManager", attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("studyAccounts"),
})
@NamedEntityGraph(name = "Study.withAccount", attributeNodes = {
        @NamedAttributeNode("studyAccounts"),
})
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study {

    @Id @GeneratedValue
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "study",
                cascade = CascadeType.PERSIST,
          orphanRemoval = true)
    private Set<StudyAccount> studyAccounts = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;
    private String shortDescription;

    @Lob
    private String fullDescription;

    @Lob
    private String image;

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<StudyTag> tags = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<StudyZone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;
    private LocalDateTime closedDateTime;
    private LocalDateTime recruitingUpdatedDateTime;
    private boolean recruiting;
    private boolean published;
    private boolean closed;
    private boolean useBanner;

    public Set<Account> getManagers() {
        return studyAccounts.stream()
                .filter(studyAccount -> studyAccount.getRole().equals(StudyAccount.Role.MANAGER))
                .map(StudyAccount::getAccount)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Account> getMembers() {
        return studyAccounts.stream()
                .filter(studyAccount -> studyAccount.getRole().equals(StudyAccount.Role.MEMBER))
                .map(StudyAccount::getAccount)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void addManager(StudyAccount studyAccount) {
        studyAccount.setRole(StudyAccount.Role.MANAGER);
        studyAccount.setStudy(this);
        getStudyAccounts().add(studyAccount);
    }

    public boolean isManagerOf(AccountDto accountDto) {
        long count = studyAccounts.stream()
                .filter(studyAccount -> Objects.equals(studyAccount.getRole(), StudyAccount.Role.MANAGER))
                .filter(studyAccount -> accountDto.getEmail().equals(studyAccount.getAccount().getEmail()))
                .count();

        return count > 0;
    }

    public void publish() {
        if (!this.closed && !this.published) {
            this.setPublished(true);
            this.setPublishedDateTime(LocalDateTime.now());
        } else {
            throw new RuntimeException();
        }
    }

    public void close() {
        if (this.published && !this.closed) {
            this.setClosed(true);
            this.setClosedDateTime(LocalDateTime.now());
        } else {
            throw new RuntimeException();
        }
    }

    public boolean startRecruit() {
        if (canUpdateRecruit()) {
            this.setRecruiting(true);
            this.setRecruitingUpdatedDateTime(LocalDateTime.now());
            return true;
        } else
            return false;
    }

    private boolean canUpdateRecruit() {
        return recruitingUpdatedDateTime.plusHours(3L).isBefore(LocalDateTime.now());
    }


    public boolean isRemovable() {
         return !published;
    }
}
