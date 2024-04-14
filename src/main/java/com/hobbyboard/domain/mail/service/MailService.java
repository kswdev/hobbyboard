package com.hobbyboard.domain.mail.service;

import com.hobbyboard.domain.account.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate redisTemplate;

    public void sendSignUpConfirmEmail(Account newAccount) {

        String token = setEmailCheckToken(newAccount.getEmail());

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + token +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public String setEmailCheckToken(String email) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String token = generatedEmailCheckToken();

        ops.set(email, token, 5, TimeUnit.MINUTES);
        return token;
    }

    private String generatedEmailCheckToken() {
        return UUID.randomUUID().toString();
    }

    public boolean isValidToken(String email, String token) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return Objects.equals(ops.get(email), token);
    }

    public void sendPasswordChangeEmail(String email) {

        String token = setEmailCheckToken(email);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("비밀번호 확인 인증");
        mailMessage.setText("/login-by-email?token=" + token +
                "&email=" + email);
        javaMailSender.send(mailMessage);

    }
}
