package com.hobbyboard.domain.event.service;

import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EventReadService {

    private final EventRepository eventRepository;

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> findByStudyIdOrderByStartDateTime(Long id) {
        return eventRepository.findByStudyIdOrderByStartDateTime(id);
    }
}
