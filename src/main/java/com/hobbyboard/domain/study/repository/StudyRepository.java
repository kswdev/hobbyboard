package com.hobbyboard.domain.study.repository;

import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyZone;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @Query("select sz from StudyZone sz where sz.study.id = :studyId and sz.zone.id = :zoneId")
    StudyZone findByStudyIdAndZoneId(Long studyId, Long zoneId);

    @EntityGraph(value = "Study.withTagAndManager", type = EntityGraph.EntityGraphType.LOAD)
    Study findAccountWithTagByPath(String path);

    @EntityGraph(value = "Study.withZoneAndManager", type = EntityGraph.EntityGraphType.LOAD)
    Study findAccountWithZoneByPath(String path);
}

