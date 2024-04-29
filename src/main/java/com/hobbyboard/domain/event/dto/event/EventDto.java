package com.hobbyboard.domain.event.dto.event;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.dto.security.UserAccount;
import com.hobbyboard.domain.event.dto.EventType;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.study.dto.StudyDto;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class EventDto implements Serializable {

    private Long id;

    private StudyDto study;

    private AccountDto createdBy;

    private String title;

    private String description;

    private LocalDateTime createdDateTime;

    private LocalDateTime endEnrollmentDateTime;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private Integer limitOfEnrollments;

    private List<EnrollmentDto> enrollments = new ArrayList<>();

    private EventType eventType;

    public static EventDto fromWithEnrollments(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .study(StudyDto.from(event.getStudy()))
                .createdBy(AccountDto.from(event.getCreateBy()))
                .enrollments(event.getEnrollments().stream()
                        .map(EnrollmentDto::from)
                        .toList())
                .title(event.getTitle())
                .description(event.getDescription())
                .createdDateTime(event.getCreatedDateTime())
                .endEnrollmentDateTime(event.getEndEnrollmentDateTime())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .limitOfEnrollments(event.getLimitOfEnrollments())
                .eventType(event.getEventType())
                .build();
    }

    public static EventDto from(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .study(StudyDto.from(event.getStudy()))
                .createdBy(AccountDto.from(event.getCreateBy()))
                .title(event.getTitle())
                .description(event.getDescription())
                .createdDateTime(event.getCreatedDateTime())
                .endEnrollmentDateTime(event.getEndEnrollmentDateTime())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .limitOfEnrollments(event.getLimitOfEnrollments())
                .eventType(event.getEventType())
                .build();
    }

    private boolean isAlreadyEnrolled(UserAccount userAccount) {
        AccountDto accountDto = userAccount.getAccount();
        for (EnrollmentDto e : this.enrollments) {
            if (e.getAccount().equals(accountDto))
                return true;
        }

        return false;
    }

    private boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    public boolean isAttended(UserAccount userAccount) {
        AccountDto accountDto = userAccount.getAccount();
        for (EnrollmentDto e : this.enrollments) {
            if (e.getAccount().equals(accountDto) && e.isAttended()) {
                return true;
            }
        }

        return false;
    }

    public boolean isDisEnrollableFor(UserAccount userAccount) {
        return isNotClosed() && !isAttended(userAccount) && isAlreadyEnrolled(userAccount);
    }

    public boolean isEnrollableFor(UserAccount userAccount) {
        return isNotClosed() && !isAttended(userAccount) && !isAlreadyEnrolled(userAccount);
    }

    public int numberOfRemainSpots() {
        return this.limitOfEnrollments - (int) this.enrollments.stream().filter(EnrollmentDto::isAccepted).count();
    }

    public long getNumberOfAcceptedEnrollments() {
        return this.enrollments.stream().filter(EnrollmentDto::isAccepted).count();
    }

    public boolean canAccept(EnrollmentDto enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    public boolean canReject(EnrollmentDto enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }
}
