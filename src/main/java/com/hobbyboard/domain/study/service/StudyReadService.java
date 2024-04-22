package com.hobbyboard.domain.study.service;

import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.repository.StudyAccountRepository;
import com.hobbyboard.domain.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyReadService {

    private final StudyRepository studyRepository;
    private final StudyAccountRepository studyAccountRepository;

    public Study findByPath(String path) {
        return studyRepository.findByPath(path);
    }
}
