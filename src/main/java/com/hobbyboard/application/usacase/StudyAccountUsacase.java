package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.service.AccountWriteService;
import com.hobbyboard.domain.study.dto.StudyDto;
import com.hobbyboard.domain.study.dto.StudyForm;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyAccount;
import com.hobbyboard.domain.study.entity.StudyTag;
import com.hobbyboard.domain.study.entity.StudyZone;
import com.hobbyboard.domain.study.event.StudyCreatedEvent;
import com.hobbyboard.domain.study.service.StudyAccountReadService;
import com.hobbyboard.domain.study.service.StudyReadService;
import com.hobbyboard.domain.study.service.StudyWriteService;
import com.hobbyboard.domain.tag.dto.TagForm;
import com.hobbyboard.domain.tag.entity.Tag;
import com.hobbyboard.domain.tag.service.TagService;
import com.hobbyboard.domain.zone.dto.request.ZoneForm;
import com.hobbyboard.domain.zone.entity.Zone;
import com.hobbyboard.domain.zone.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class StudyAccountUsacase {

    private final StudyAccountReadService studyAccountReadService;
    private final AccountWriteService accountWriteService;
    private final StudyWriteService studyWriteService;
    private final StudyReadService studyReadService;
    private final ZoneService zoneService;
    private final TagService tagService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Study createNewStudy(Study study, Account account) {

        //엔티티 조회
        Account findAccount = accountWriteService.findById(account.getId());

        //스터디 회원 생성
        StudyAccount newStudyAccount = new StudyAccount(findAccount);

        //스터디 생성
        study.addManager(newStudyAccount);

        return studyWriteService.save(study);
    }

    public StudyDto findByPath(String path) {
        Study study = studyReadService.findWithAllByPath(path);
        return StudyDto.fromWithAll(study);
    }

    public Study getStudyToUpdate(AccountDto accountDto, String path) {
        Study study = this.getStudy(path);

        checkIfManager(accountDto, study);

        return study;
    }

    private Study getStudy(String path) {
        Study study = studyReadService.findWithAllByPath(path);

        if (study == null)
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");

        return study;
    }

    @Transactional
    public void updateStudyImage(AccountDto accountDto, String path, String image) {
        Study study = this.getStudyToUpdate(accountDto, path);
        study.setImage(image);
    }

    @Transactional
    public void addTag(TagForm tagForm, String path) {
        Study study = studyReadService.findStudyToUpdateTag(path);

        Tag tag = tagService.findByTitle(tagForm.getTagTitle()).orElseGet(() ->
                  tagService.save(new Tag(tagForm.getTagTitle())));

        StudyTag studyTag = StudyTag.builder()
                .study(study)
                .tag(tag)
                .build();

        study.getTags().add(studyTag);
    }

    @Transactional
    public void removeTag(TagForm tagForm, String path) {
        Study study = studyReadService.findStudyToUpdateTag(path);
        Tag tag = tagService.findByTitle(tagForm.getTagTitle()).get();

        StudyTag studyTag = studyWriteService.findByStudyIdAndTagId(study.getId(), tag.getId());
        if (!ObjectUtils.isEmpty(studyTag))
            study.getTags().remove(studyTag);
    }

    @Transactional
    public void addZones(String path, ZoneForm zoneForm) {
        Study study = studyReadService.findStudyToUpdateZone(path);
        Zone zone = zoneService.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        StudyZone studyZone = StudyZone.builder()
                .study(study)
                .zone(zone)
                .build();

        study.getZones().add(studyZone);
    }

    @Transactional
    public void removeZones(String path, ZoneForm zoneForm) {
        Study study = studyReadService.findStudyToUpdateZone(path);
        Zone zone = zoneService.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        StudyZone studyZone = studyReadService.findByStudyIdAndZoneId(study.getId(), zone.getId());

        if (!ObjectUtils.isEmpty(studyZone))
            study.getZones().remove(studyZone);
    }

    @Transactional
    public void startPublish(String path, AccountDto accountDto) {
        Study study = studyReadService.findWithAccountByPath(path);

        checkIfManager(accountDto, study);

        study.publish();
        this.eventPublisher.publishEvent(new StudyCreatedEvent(study));
    }

    @Transactional
    public void closeStudy(String path, AccountDto accountDto) {
        Study study = studyReadService.findWithAccountByPath(path);

        checkIfManager(accountDto, study);

        study.close();
    }

    @Transactional
    public boolean startRecruit(String path, AccountDto accountDto) {
        Study study = studyReadService.findWithAccountByPath(path);

        checkIfManager(accountDto, study);

        return study.startRecruit();
    }

    @Transactional
    public void stopRecruit(String path, AccountDto accountDto) {
        Study study = studyReadService.findWithAccountByPath(path);

        checkIfManager(accountDto, study);

        study.setRecruiting(false);
        study.setRecruitingUpdatedDateTime(LocalDateTime.now());
    }

    @Transactional
    public void updateStudyPath(Study study, String newPath) {
        study.setPath(newPath);
    }

    @Transactional
    public StudyDto updateStudyTitle(String path, String newTitle, AccountDto accountDto) {
        Study study = studyReadService.findWithAccountByPath(path);

        checkIfManager(accountDto, study);

        study.setTitle(newTitle);

        return StudyDto.fromWithAll(study);
    }

    @Transactional
    public void removeStudy(String path, AccountDto accountDto) {
        Study study = studyReadService.findWithAccountByPath(path);
        checkIfManager(accountDto, study)
        ;
        if (study.isRemovable())
            studyWriteService.remove(study);
        else
            throw new IllegalArgumentException("스터디를 삭제할 수 없습니다.");
    }

    public void checkIfManager(AccountDto accountDto, Study study) {
        if (!study.isManagerOf(accountDto))
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
    }

    @Transactional
    public void joinStudy(AccountDto accountDto, String path) {
        //엔티티 조회
        Account account = accountWriteService.findById(accountDto.getId());

        //스터디 회원 생성
        StudyAccount newStudyAccount = new StudyAccount(account);

        //스터디 생성
        Study study = studyReadService.findWithAccountByPath(path);
        study.addMember(newStudyAccount);
    }

    @Transactional
    public void leaveStudy(AccountDto accountDto, String path) {

        Study study = studyReadService.findWithAccountByPath(path);

        StudyAccount studyAccount = studyAccountReadService.findByStudyIdAndAccountId(study.getId(), accountDto.getId());

        study.removeMember(studyAccount);
    }

    public Study getStudyWithAccountToUpdate(AccountDto accountDto, String path) {
        Study study = studyReadService.findWithAccountByPath(path);

        checkIfManager(accountDto, study);
        return study;
    }
}
