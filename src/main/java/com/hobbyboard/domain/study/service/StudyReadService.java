package com.hobbyboard.domain.study.service;

import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyZone;
import com.hobbyboard.domain.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyReadService {

    private final StudyRepository studyRepository;

    public Study findWithAllByPath(String path) {
        return studyRepository.findWithAllByPath(path);
    }

    public StudyZone findByStudyIdAndZoneId(Long studyId, Long zoneId) {
        return studyRepository.findByStudyIdAndZoneId(studyId, zoneId);
    }

    public Study findStudyToUpdateTag(String path) {
        return studyRepository.findWithTagAndAccountByPath(path);
    }

    public Study findStudyToUpdateZone(String path) {
        return studyRepository.findWithZoneAndAccountByPath(path);
    }

    public Study findStudyWithByPath(String path) {
        return studyRepository.findWithAccountByPath(path);
    }

    public Study findByPath(String path) {
        return studyRepository.findByPath(path);
    }

    public boolean isValidPath(String newPath) {
        Study byPath = findByPath(newPath);
        return !ObjectUtils.isEmpty(byPath);
    }
}
