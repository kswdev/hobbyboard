package com.hobbyboard.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    /* DispatcherServlet 하나당 하나씩 등록 가능 */
    @Bean
    public MessageSource messageSource() {

        //로케일마다 따로 배치한 리소스 번들을 이용해 메시지를 해석 아래 구현체는 basename 이 messages 인 리소스 번들을 로드한다.
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }
}
