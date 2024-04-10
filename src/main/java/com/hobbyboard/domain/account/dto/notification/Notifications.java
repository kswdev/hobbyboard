package com.hobbyboard.domain.account.dto.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter @Setter
public class Notifications implements Serializable {

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByWeb;
    private boolean studyUpdatedByEmail;


    @Builder
    public Notifications(boolean studyCreatedByEmail, boolean studyCreatedByWeb, boolean studyEnrollmentResultByEmail, boolean studyEnrollmentResultByWeb, boolean studyUpdatedByWeb, boolean studyUpdatedByEmail) {
        this.studyCreatedByEmail = studyCreatedByEmail;
        this.studyCreatedByWeb = studyCreatedByWeb;
        this.studyEnrollmentResultByEmail = studyEnrollmentResultByEmail;
        this.studyEnrollmentResultByWeb = studyEnrollmentResultByWeb;
        this.studyUpdatedByWeb = studyUpdatedByWeb;
        this.studyUpdatedByEmail = studyUpdatedByEmail;
    }
}
