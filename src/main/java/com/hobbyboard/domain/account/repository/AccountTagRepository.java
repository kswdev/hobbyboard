package com.hobbyboard.domain.account.repository;

import com.hobbyboard.domain.account.entity.AccountTag;
import com.hobbyboard.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface AccountTagRepository extends JpaRepository<AccountTag, Long> {

    @Query("select at from AccountTag at where at.tag.id = :tagId and at.account.id = :accountId")
    AccountTag findByAccountIdAndTagId(Long accountId, Long tagId);

    Set<AccountTag> findByAccountId(Long id);

    @Transactional
    @Modifying
    @Query("delete from AccountTag at where at.account.id = :id")
    void deleteByAccountId(Long id);

    @Query("select at from AccountTag at where at.tag in (:tags)")
    Set<AccountTag> findByTags(Set<Tag> tags);
}
