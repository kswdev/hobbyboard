package com.hobbyboard.domain.event.dto.event;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.event.entity.Enrollment;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class EnrollmentDto {

    private Long id;
    private AccountDto account;
    private LocalDateTime enrolledAt;
    private boolean accepted;
    private boolean attended;

    public static EnrollmentDto from (Enrollment enrollment) {
        return EnrollmentDto.builder()
                .id(enrollment.getId())
                .account(AccountDto.from(enrollment.getAccount()))
                .enrolledAt(enrollment.getEnrolledAt())
                .accepted(enrollment.isAccepted())
                .attended(enrollment.isAttended())
                .build();
    }
}
