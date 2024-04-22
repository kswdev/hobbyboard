package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.service.AccountWriteService;
import com.hobbyboard.domain.study.dto.StudyDto;
import com.hobbyboard.domain.study.dto.StudyForm;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyAccount;
import com.hobbyboard.domain.study.service.StudyReadService;
import com.hobbyboard.domain.study.service.StudyWriteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;



@Component
@RequiredArgsConstructor
public class StudyAccountUsacase {

    private final AccountWriteService accountWriteService;
    private final StudyWriteService studyWriteService;
    private final StudyReadService studyReadService;
    private final ModelMapper modelMapper;

    @Transactional
    public Study createNewStudy(StudyForm studyForm, AccountDto accountDto) {

        //엔티티 조회
        Account account = accountWriteService.findById(accountDto.getId());

        //스터디 회원 생성
        StudyAccount newStudyAccount = new StudyAccount(account);

        //스터디 생성
        Study study = modelMapper.map(studyForm, Study.class);
        study.addManager(newStudyAccount);

        return studyWriteService.save(study);
    }

    public StudyDto findByPath(String path) {
        Study study = studyReadService.findByPath(path);
        return StudyDto.from(study);
    }

    public StudyDto getStudyToUpdate(AccountDto accountDto, String path) {
        StudyDto studyDto = StudyDto.from(this.getStudy(path));
        if (!studyDto.isManagerOf(accountDto))
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");

        return studyDto;
    }

    private Study getStudy(String path) {
        Study study = studyReadService.findByPath(path);
        if (study == null)
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");

        return study;
    }
}
