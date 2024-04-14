package com.hobbyboard.domain.tag.service;

import com.hobbyboard.domain.tag.entity.Tag;
import com.hobbyboard.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Optional<Tag> findByTitle(String title) {
        return tagRepository.findByTitle(title);
    }

    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }
}
