package com.hobbyboard.domain.account.repository;

import com.hobbyboard.domain.account.entity.AccountTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountTagRepository extends JpaRepository<AccountTag, Long> {
    AccountTag findByTagId(Long id);

    @Query("delete from AccountTag at where at.tag.id = :tagId and at.account.id = :accountId")
    void deleteByAccountIdAndTagId(Long tagId, Long accountId);

    @Query("select at from AccountTag at where at.tag.id = :tagId and at.account.id = :accountId")
    Optional<AccountTag> findByAccountIdAndTagId(Long accountId, Long ta);
}
