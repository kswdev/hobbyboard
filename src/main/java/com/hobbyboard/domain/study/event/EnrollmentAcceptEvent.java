package com.hobbyboard.domain.study.event;

import com.hobbyboard.domain.event.entity.Enrollment;

public class EnrollmentAcceptEvent extends EnrollmentEvent{
    public EnrollmentAcceptEvent(Enrollment enrollment) {
        super(enrollment, "'" + enrollment.getEvent().getTitle() + "' 모임에 초대되었습니다.");
    }
}
