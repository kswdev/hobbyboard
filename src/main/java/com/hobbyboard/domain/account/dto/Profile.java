package com.hobbyboard.domain.account.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Profile {

    private String bio;
    private String url;
    private String occupation;
    private String location;

    @Builder
    public Profile(String bio, String url, String occupation, String location) {
        this.bio = bio;
        this.url = url;
        this.occupation = occupation;
        this.location = location;
    }

    public static Profile fromAccountDto(AccountDto accountDto) {
        return Profile.builder()
                .bio(accountDto.getBio())
                .url(accountDto.getUrl())
                .occupation(accountDto.getOccupation())
                .location(accountDto.getLocation())
                .build();
    }
}
