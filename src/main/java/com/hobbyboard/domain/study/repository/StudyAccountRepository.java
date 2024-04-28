package com.hobbyboard.domain.study.repository;

import com.hobbyboard.domain.study.entity.StudyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyAccountRepository extends JpaRepository<StudyAccount, Long> {

    @Query("select sa from StudyAccount sa where sa.study.id = :studyId and sa.account.id = :accountId")
    StudyAccount findByAccountIdAndStudyId(Long studyId, Long accountId);
}
