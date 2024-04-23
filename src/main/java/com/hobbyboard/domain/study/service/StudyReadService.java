package com.hobbyboard.domain.study.service;

import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyZone;
import com.hobbyboard.domain.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Study findByPath(String path) {
        return studyRepository.findByPath(path);
    }

    public Study findStudyToUpdateTag(String path) {
        return studyRepository.findStudyWithTagByPath(path);
    }

    public Study findStudyToUpdateZone(String path) {
        return studyRepository.findStudyWithZoneByPath(path);
    }

    public Study findStudyWithByPath(String path) {
        return studyRepository.findStudyWithByPath(path);
    }
}
