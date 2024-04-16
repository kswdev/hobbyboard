package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.service.AccountWriteService;
import com.hobbyboard.domain.study.dto.StudyForm;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyAccount;
import com.hobbyboard.domain.study.service.StudyWriteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StudyAccountUsacase {

    private final AccountWriteService accountWriteService;
    private final StudyWriteService studyWriteService;
    private final ModelMapper modelMapper;

    @Transactional
    public Study createNewStudy(StudyForm studyForm, AccountDto accountDto) {

        //엔티티 조회
        Account account = accountWriteService.findById(accountDto.getId());

        //스터디 생성
        Study study = modelMapper.map(studyForm, Study.class);

        //스터디 회원 생성
        StudyAccount newStudyAccount = StudyAccount.createStudy(study, account);

        studyWriteService.save(newStudyAccount);

        return study;
    }
}
