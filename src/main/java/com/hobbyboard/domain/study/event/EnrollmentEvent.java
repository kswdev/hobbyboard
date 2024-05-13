package com.hobbyboard.domain.study.event;

import com.hobbyboard.domain.event.entity.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnrollmentEvent {

    private final Enrollment enrollment;
    private final String message;
}
