package com.hobbyboard.domain.account.converter;

import com.hobbyboard.domain.account.dto.RoleType;
import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class UserRoleConverter implements AttributeConverter<Set<RoleType>, String> {
    @Override
    public String convertToDatabaseColumn(Set<RoleType> roleTypes) {
        if (roleTypes.isEmpty()) return null;
        return roleTypes.stream()
                .map(RoleType::getName)
                .collect(Collectors.joining(","));
    }

    @Override
    public Set<RoleType> convertToEntityAttribute(String dbData) {
        if (dbData == null) return Collections.emptySet();
        try {
            return RoleType.fromCode(dbData);
        } catch (IllegalArgumentException e) {
            log.error("failure to convert cause unexpected code[{}]", dbData, e);
        }
        return Collections.emptySet();
    }
}
