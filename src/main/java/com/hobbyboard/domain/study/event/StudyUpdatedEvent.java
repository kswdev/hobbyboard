package com.hobbyboard.domain.study.event;

import com.hobbyboard.domain.study.entity.Study;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StudyUpdatedEvent {

    private Study study;
    private String message;
}
