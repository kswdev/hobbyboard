package com.hobbyboard.domain.account.dto.passwordForm;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class PasswordForm {

     @NotBlank
     @Length(min = 8, max = 50)
     private String newPassword;

     @NotBlank
     @Length(min = 8, max = 50)
     private String newPasswordConfirm;

     public boolean passwordCheck() {
          return newPassword.equals(newPasswordConfirm);
     }
}
