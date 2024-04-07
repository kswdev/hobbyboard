package com.hobbyboard.domain.account.dto.passwordForm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class PasswordFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;
        if(!passwordForm.passwordCheck())
            errors.rejectValue("newPasswordConfirm", "wrong.value", new Object[]{passwordForm.getNewPasswordConfirm()}, "입력한 새 패스워드가 일치하지 않습니다.");
    }
}
