package com.hobbyboard.domain.study.event;

import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.repository.StudyRepository;
import com.hobbyboard.domain.study.service.StudyReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyRepository.findWithAllById(studyCreatedEvent.getStudy().getId());

        //TODO 이메일 보내거나 DB에 저장
    }
}
