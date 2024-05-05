package com.hobbyboard.domain.study;

import com.hobbyboard.application.usacase.StudyAccountUsacase;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.service.StudyReadService;
import com.hobbyboard.domain.study.service.StudyWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyFactory {

    @Autowired StudyReadService studyReadService;
    @Autowired StudyWriteService studyWriteService;
    @Autowired StudyAccountUsacase studyAccountUsacase;

    public Study createStudy(String path, Account manager) {
        Study study = new Study();
        study.setPath(path);
        studyAccountUsacase.createNewStudy(study, manager);
        return study;
    }
}
