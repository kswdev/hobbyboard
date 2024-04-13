package com.hobbyboard.domain.tag.dto;

import com.hobbyboard.domain.tag.entity.Tag;
import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
public class TagDto implements Serializable {

    private Long id;

    private String title;


    public static TagDto from (Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .title(tag.getTitle())
                .build();
    }
}
