package com.hobbyboard.domain.mail.service;

import com.hobbyboard.domain.account.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + generatedEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);

        setEmailCheckToken(newAccount);
    }

    public String setEmailCheckToken(Account account) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String token = generatedEmailCheckToken();

        ops.set(account.getEmail(), token, 5, TimeUnit.MINUTES);
        return token;
    }

    private String generatedEmailCheckToken() {
        return UUID.randomUUID().toString();
    }

    public boolean isValidToken(Account account, String token) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return Objects.equals(ops.get(account.getEmail()), token);
    }
}
