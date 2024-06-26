package com.hobbyboard.domain.event.service;

import com.hobbyboard.domain.event.entity.Enrollment;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.event.repository.EnrollmentRepository;
import com.hobbyboard.domain.event.repository.EventRepository;
import com.hobbyboard.domain.study.event.StudyUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class EventWriteService {

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void save(Enrollment enrollment) {
        enrollmentRepository.save(enrollment);
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public void delete(Event event) {
        eventRepository.delete(event);
        eventPublisher.publishEvent(new StudyUpdatedEvent(event.getStudy(),
                "'" + event.getTitle() + "' 모임을 취소했습니다."));
    }
}
