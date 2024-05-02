package com.hobbyboard.domain.event.repository;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.event.entity.Enrollment;
import com.hobbyboard.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Enrollment findByEventAndAccount(Event event, Account account);
}
