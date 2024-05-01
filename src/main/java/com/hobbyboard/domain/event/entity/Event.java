package com.hobbyboard.domain.event.entity;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.dto.security.UserAccount;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.event.dto.EventType;
import com.hobbyboard.domain.event.dto.event.EnrollmentDto;
import com.hobbyboard.domain.study.entity.Study;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(name = "Event.withAccountAndStudy", attributeNodes = {
        @NamedAttributeNode("study"),
        @NamedAttributeNode("createBy")
})
@NamedEntityGraph(name = "Event.withEnrollments", attributeNodes = {
        @NamedAttributeNode("enrollments")
})
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Event {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;


    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private Integer limitOfEnrollments;

    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST)
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public boolean isAlreadyEnrolled(Account account) {

        for (Enrollment e : this.enrollments)
            if (e.getAccount().equals(account))
                return true;

        return false;
    }

    private long getNumberOfAcceptedEnrollments() {
        return this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    public void addEnrollment(Enrollment enrollment) {
        this.getEnrollments().add(enrollment);
    }

    private void addEnrollmentAndAttend(Enrollment enrollment) {
        enrollment.setAttended(true);
        this.addEnrollment(enrollment);
    }

    private void addEnrollmentAndAccept(Enrollment enrollment) {
        enrollment.setAccepted(true);
        this.addEnrollment(enrollment);
    }

    public void submitEnrollment(Enrollment enrollment) {

        if (this.eventType == EventType.CONFIRMATIVE)
            addEnrollmentAndAttend(enrollment);

        else if (this.eventType == EventType.FCFS) {

            if (this.limitOfEnrollments > getNumberOfAcceptedEnrollments())
                addEnrollmentAndAccept(enrollment);
            else
                addEnrollmentAndAttend(enrollment);
        }

    }

    private boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    public boolean canSubmit(Account account) {
        return !isAlreadyEnrolled(account) && isNotClosed();
    }
}
