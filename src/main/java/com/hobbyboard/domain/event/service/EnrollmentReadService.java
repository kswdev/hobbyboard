package com.hobbyboard.domain.event.service;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.event.entity.Enrollment;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.event.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EnrollmentReadService {

    private final EnrollmentRepository enrollmentRepository;

    public Enrollment findByEventAndAccount(Event event, Account account) {
        return enrollmentRepository.findByEventAndAccount(event, account);
    }

    public Enrollment findById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow();
    }
}
