package com.hobbyboard.domain.study.event;

import com.hobbyboard.domain.study.entity.Study;
import lombok.Getter;

@Getter
public class StudyCreatedEvent{

    private Study study;

    public StudyCreatedEvent(Study newStudy) {
        this.study = newStudy;
    }
}
