package com.hobbyboard.domain.study.repository;

import com.hobbyboard.domain.study.entity.StudyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyAccountRepository extends JpaRepository<StudyAccount, Long> {

}
