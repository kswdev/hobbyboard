package com.hobbyboard.domain.study.repository;

import com.hobbyboard.domain.study.entity.StudyTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyTagRepository extends JpaRepository<StudyTag, Long> {

    @Query("select st from StudyTag st where st.study.id = :studyId and st.tag.id = :tagId")
    StudyTag findByStudyIdAndTagId(Long studyId, Long tagId);
}
