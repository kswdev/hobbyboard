package com.hobbyboard.domain.account.repository;

import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.entity.QAccount;
import com.hobbyboard.domain.tag.entity.Tag;
import com.hobbyboard.domain.zone.entity.Zone;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> , QuerydslPredicateExecutor<Account> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Account findByEmail(String email);

    Account findByNickname(String nickname);

    @Modifying
    @Query("delete from Account a where a.nickname = :nickname")
    void deleteByNickname(String nickname);
}
