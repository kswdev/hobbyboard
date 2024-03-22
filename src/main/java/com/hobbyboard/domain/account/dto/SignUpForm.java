package com.hobbyboard.domain.account.dto;

import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class JoinAccountResponse {
    private String email;
    private String nickname;
    private String password;
}
