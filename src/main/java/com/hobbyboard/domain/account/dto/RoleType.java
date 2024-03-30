package com.hobbyboard.domain.account.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum RoleType {
    USER("USER"),
    ADMIN("ADMIN");

    @Getter
    private final String name;

    RoleType(String name) {
        this.name = name;
    }

    public static Set<RoleType> fromCode(String dbData) {
        String[] array = dbData.split(",");
        Set<String> roleTypes = new HashSet<>(Arrays.asList(array));
        return Arrays.stream(RoleType.values())
                .filter(v -> roleTypes.contains(v.getName()))
                .collect(Collectors.toUnmodifiableSet());
    }
}