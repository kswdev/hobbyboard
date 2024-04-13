package com.hobbyboard.domain.account.repository;

import com.hobbyboard.domain.account.dto.RoleType;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAccountSecurityContextFactory.class)
public @interface WithAccount {

    long id() default 1L;
    String email() default "email@naver.com";
    String password() default "password";
    String nickname() default "nickname";
    RoleType role() default RoleType.USER;
}
