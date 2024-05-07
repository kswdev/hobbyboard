package com.hobbyboard.domain.account.predicate;

import com.hobbyboard.domain.account.entity.AccountTag;
import com.hobbyboard.domain.account.entity.AccountZone;
import com.hobbyboard.domain.account.entity.QAccount;
import com.querydsl.core.types.Predicate;

import java.util.Set;

public class AccountPredicates {

    public static Predicate findByTagsAndZones(Set<AccountTag> tags, Set<AccountZone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.accountTags.any().in(tags));
    }
}
