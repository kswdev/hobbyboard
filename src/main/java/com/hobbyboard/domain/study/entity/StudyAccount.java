package com.hobbyboard.domain.study.entity;

import com.hobbyboard.domain.account.entity.Account;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class StudyAccount {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account account;

    public static StudyAccount createStudy(Study study, Account account) {
        StudyAccount studyAccount = new StudyAccount();
        studyAccount.setStudy(study);
        studyAccount.setAccount(account);

        return studyAccount;
    }

    public void setStudy(Study study) {
        this.study = study;
        study.getManagers().add(this);
    }
}
