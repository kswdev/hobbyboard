package com.hobbyboard.domain.study.service;

import com.hobbyboard.domain.study.dto.StudyDescriptionForm;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyTag;
import com.hobbyboard.domain.study.repository.StudyRepository;
import com.hobbyboard.domain.study.repository.StudyTagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyWriteService {
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
}
