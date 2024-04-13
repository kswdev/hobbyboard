package com.hobbyboard.domain.account.dto;

import com.hobbyboard.domain.account.dto.account.AccountDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Getter @Setter
public class Profile {

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    private String location;
    private String profileImage;

    @Builder
    public Profile(String bio, String url, String occupation, String location, String profileImage) {
        this.bio = bio;
        this.url = url;
        this.occupation = occupation;
        this.location = location;
        this.profileImage = profileImage;
    }

    public static Profile fromAccountDto(AccountDto accountDto) {
        return Profile.builder()
                .bio(accountDto.getBio())
                .url(accountDto.getUrl())
                .occupation(accountDto.getOccupation())
                .location(accountDto.getLocation())
                .profileImage(accountDto.getProfileImage())
                .build();
    }
}
