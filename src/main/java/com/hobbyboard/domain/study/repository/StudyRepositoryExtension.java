package com.hobbyboard.domain.study.repository;

import com.hobbyboard.domain.study.entity.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {

    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    List<Study> findByKeyword(String keyword);
}
