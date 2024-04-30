package com.hobbyboard.domain.event.repository;

import com.hobbyboard.domain.event.entity.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @EntityGraph(value = "Event.withAccountAndStudy", type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findByStudyIdOrderByStartDateTime(Long id);
}
