package com.hobbyboard.domain.study.service;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.study.dto.StudyDescriptionForm;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyAccount;
import com.hobbyboard.domain.study.entity.StudyTag;
import com.hobbyboard.domain.study.event.StudyUpdatedEvent;
import com.hobbyboard.domain.study.repository.StudyRepository;
import com.hobbyboard.domain.study.repository.StudyTagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyWriteService {
    private final ApplicationEventPublisher eventPublisher;
    private final StudyTagRepository studyTagRepository;
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

    public Study save(Study study) {
        return studyRepository.save(study);
    }

    public Study updateStudyDescription(StudyDescriptionForm studyForm) {

        Study study = studyRepository.findById(studyForm.getStudyDescriptionId())
                .orElseThrow(IllegalArgumentException::new);

        modelMapper.map(studyForm, study);
        eventPublisher.publishEvent(new StudyUpdatedEvent(study, "스터디 소개를 수정했습니다."));
        return study;
    }

    public void enableStudyBanner(String path) {
        Study byPath = studyRepository.findWithAllByPath(path);
        byPath.setUseBanner(true);
    }

    public void disableStudyBanner(String path) {
        Study byPath = studyRepository.findWithAllByPath(path);
        byPath.setUseBanner(false);
    }

    public StudyTag findByStudyIdAndTagId(Long studyId, Long tagId) {
        return studyTagRepository.findByStudyIdAndTagId(studyId, tagId);
    }

    public void remove(Study study) {
        studyRepository.delete(study);
    }
}
