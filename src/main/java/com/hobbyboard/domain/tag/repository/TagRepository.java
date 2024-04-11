package com.hobbyboard.domain.tag.repository;

import com.hobbyboard.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {


    Optional<Tag> findByTitle(String title);
}
