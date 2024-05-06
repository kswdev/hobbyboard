package com.hobbyboard.application.usacase;

import com.hobbyboard.domain.account.AccountFactory;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.repository.AccountRepository;
import com.hobbyboard.domain.account.repository.WithAccount;
import com.hobbyboard.domain.event.dto.EventType;
import com.hobbyboard.domain.event.entity.Enrollment;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.event.repository.EnrollmentRepository;
import com.hobbyboard.domain.event.repository.EventRepository;
import com.hobbyboard.domain.event.service.EnrollmentReadService;
import com.hobbyboard.domain.event.service.EventReadService;
import com.hobbyboard.domain.event.service.EventWriteService;
import com.hobbyboard.domain.study.StudyFactory;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.repository.StudyAccountRepository;
import com.hobbyboard.domain.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
class EventEnrollmentUsacaseTest {


    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired EventRepository eventRepository;
    @Autowired EnrollmentRepository enrollmentRepository;
    @Autowired StudyAccountRepository studyAccountRepository;
    @Autowired StudyRepository studyRepository;
    @Autowired EventReadService eventReadService;
    @Autowired EventWriteService eventWriteService;
    @Autowired EnrollmentReadService enrollmentReadService;
    @Autowired AccountFactory accountFactory;
    @Autowired StudyFactory studyFactory;
    @Autowired ModelMapper modelMapper;

    private static final String STUDY_PATH = "study-test";

    @AfterEach
    void After() {
        eventRepository.deleteAll();
        studyAccountRepository.deleteAll();
        accountRepository.deleteAll();
        studyRepository.deleteAll();
    }

    @WithAccount
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @Test
    void newEnrollment_to_FCFS_event_accepted() throws Exception {

        Account account = accountFactory.getAccount("nickname");
        Study study = studyFactory.createStudy(STUDY_PATH, account);
        Event event = createEvent(account, study, "title", EventType.FCFS, 2);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account ksw = accountRepository.findByNickname("nickname");
        assertTrue("모임 신청 수락 됐는지", enrollmentReadService.findByEventAndAccount(event, ksw).isAccepted());
    }

    @WithAccount
    @DisplayName("모임에 출석체크 - 정상 작동")
    @Test
    void event_checkIn() throws Exception {
        //given
        Account account = accountFactory.getAccount("nickname");
        Study study = studyFactory.createStudy(STUDY_PATH, account);
        Event event = createEvent(account, study, "title", EventType.FCFS, 2);
        Enrollment enrollment = createEnrollment(account, event);

        //then
        mockMvc.perform(get("/study/" + study.getPath() + "/events/" + event.getId() + "/enrollments/" + enrollment.getId() + "/checkin")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Enrollment foundEnrollment = enrollmentRepository.findByEventAndAccount(event, account);
        assertTrue("모임 출첵!", foundEnrollment.isAttended());
    }

    private Enrollment createEnrollment(Account account, Event event) {
        Enrollment enrollment = Enrollment.builder()
                .enrolledAt(LocalDateTime.now())
                .event(event)
                .account(account)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    private Event createEvent(Account account, Study study, String title, EventType eventType, int limit) {
        Event event = Event.builder()
                .createdDateTime(LocalDateTime.now())
                .title(title)
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusMonths(1))
                .endEnrollmentDateTime(LocalDateTime.now().plusHours(1))
                .eventType(eventType)
                .createBy(account)
                .limitOfEnrollments(limit)
                .study(study)
                .build();

        return eventWriteService.save(event);
    }
}