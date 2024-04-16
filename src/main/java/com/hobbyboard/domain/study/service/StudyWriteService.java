package com.hobbyboard.domain.study.service;

import com.hobbyboard.domain.study.entity.StudyAccount;
import com.hobbyboard.domain.study.repository.StudyAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyWriteService {
    private final StudyAccountRepository studyAccountRepository;

    public void save(StudyAccount newStudyAccount) {
        studyAccountRepository.save(newStudyAccount);
    }
}
