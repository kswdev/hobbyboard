package com.hobbyboard.domain.study.service;

import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyAccount;
import com.hobbyboard.domain.study.repository.StudyAccountRepository;
import com.hobbyboard.domain.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyWriteService {
    private final StudyAccountRepository studyAccountRepository;
    private final StudyRepository studyRepository;

    public void save(StudyAccount newStudyAccount) {
        studyAccountRepository.save(newStudyAccount);
    }

    public Study save(Study study) {
        return studyRepository.save(study);
    }
}
