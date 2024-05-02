package com.hobbyboard.domain.event.entity;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.event.dto.EventType;
import com.hobbyboard.domain.study.entity.Study;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST, orphanRemoval = true)
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

    private void addEnrollmentAndAccept(Enrollment enrollment) {
        enrollment.setAccepted(true);
        this.addEnrollment(enrollment);
    }

    public void submitEnrollment(Enrollment enrollment) {

        if (this.eventType == EventType.CONFIRMATIVE) {

            if (this.getCreateBy().equals(enrollment.getAccount()))
                addEnrollmentAndAccept(enrollment);
            else
                addEnrollment(enrollment);

        } else if (this.eventType == EventType.FCFS) {

            if (this.limitOfEnrollments > getNumberOfAcceptedEnrollments())
                addEnrollmentAndAccept(enrollment);
            else
                addEnrollment(enrollment);
        }

    }

    private boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    public boolean canSubmit(Account account) {
        return !isAlreadyEnrolled(account) && isNotClosed();
    }

    public void disenrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);

        if (this.eventType == EventType.FCFS)
            acceptRestOfEnrollments();

    }

    public long getRestOfCanBeAccepted() {
        return this.limitOfEnrollments - getNumberOfAcceptedEnrollments();
    }

    private void acceptNextEnrollments(long number) {
        this.enrollments.stream()
                .filter(enrollment -> !enrollment.isAccepted())
                .sorted(Comparator.comparing(Enrollment::getEnrolledAt))
                .limit(number)
                .forEach(nextEnrollment -> nextEnrollment.setAccepted(true));
    }

    public void acceptRestOfEnrollments() {
        long numberOf = getRestOfCanBeAccepted();
        acceptNextEnrollments(numberOf);
    }

    public void acceptEnrollment(Account account, Long enrollmentId) {

        checkIfManager(account);

        if (getRestOfCanBeAccepted() <= 0)
            throw new IllegalArgumentException("이미 모든 자리가 찾습니다.");

        this.getEnrollments().stream()
                .filter(enrollment -> enrollment.getId().equals(enrollmentId))
                .findAny()
                .ifPresent(enrollment -> enrollment.setAccepted(true));
    }

    public void rejectEnrollment(Account account, Long enrollmentId) {

        checkIfManager(account);

        this.getEnrollments().stream()
                .filter(enrollment -> enrollment.getId().equals(enrollmentId))
                .findAny()
                .ifPresent(enrollment -> enrollment.setAccepted(false));
    }

    private void checkIfManager(Account account) {
        if (!this.getCreateBy().equals(account))
            throw new IllegalArgumentException("이벤트 관리자가 아닙니다.");
    }
}
