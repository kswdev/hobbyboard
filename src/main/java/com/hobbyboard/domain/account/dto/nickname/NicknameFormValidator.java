package com.hobbyboard.domain.account.dto.nickname;

import com.hobbyboard.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class NicknameFormValidator implements Validator {

    private final AccountRepository accountRepository;
    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;
        if (!ObjectUtils.isEmpty(
                accountRepository.findByNickname(nicknameForm.getNickname())
        )) {
            errors.rejectValue("nickname", "invalid.nickname",
                                    new Object[]{nicknameForm.getNickname()}, "닉네임이 중복됩니다.");
        }
    }
}
