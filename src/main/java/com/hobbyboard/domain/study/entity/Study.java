package com.hobbyboard.domain.study.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study {

    @Id @GeneratedValue
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "study")
    private Set<StudyAccount> managers = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "study")
    private Set<StudyAccount> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;
    private String shortDescription;

    @Lob
    private String fullDescription;

    @Lob
    private String image;

    @Builder.Default
    @OneToMany(mappedBy = "study")
    private Set<StudyTag> tags = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "study")
    private Set<StudyZone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;
    private LocalDateTime closedDateTime;
    private LocalDateTime recruitingUpdatedDateTime;
    private boolean recruiting;
    private boolean published;
    private boolean closed;
    private boolean useBanner;
}
