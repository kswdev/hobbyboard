package com.hobbyboard.domain.event.service;

import com.hobbyboard.domain.event.entity.Enrollment;
import com.hobbyboard.domain.event.entity.Event;
import com.hobbyboard.domain.event.repository.EnrollmentRepository;
import com.hobbyboard.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventWriteService {

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;

    public void save(Enrollment enrollment) {
        enrollmentRepository.save(enrollment);
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }
}
