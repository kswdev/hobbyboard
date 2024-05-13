package com.hobbyboard.domain.study.event;

import com.hobbyboard.domain.event.entity.Enrollment;

public class EnrollmentRejectEvent extends EnrollmentEvent{
    public EnrollmentRejectEvent(Enrollment enrollment) {
        super(enrollment, "'" + enrollment.getEvent().getTitle() + "' 모임에 초대가 거절되었습니다.");
    }
}
