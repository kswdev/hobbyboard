package com.hobbyboard.domain.study.service;

import com.hobbyboard.domain.study.entity.StudyAccount;
import com.hobbyboard.domain.study.repository.StudyAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StudyAccountReadService {

    private final StudyAccountRepository studyAccountRepository;

    public StudyAccount findByStudyIdAndAccountId(Long studyId, Long accountId) {
        return studyAccountRepository.findByAccountIdAndStudyId(studyId, accountId);
    }
}
