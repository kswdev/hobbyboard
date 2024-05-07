package com.hobbyboard.config;

import com.hobbyboard.domain.notification.interceptor.NotificationInterceptor;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final NotificationInterceptor notificationInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> staticResourcesPath = new ArrayList<>(Arrays.stream(StaticResourceLocation.values())
                .flatMap(StaticResourceLocation::getPatterns)
                .toList());

        staticResourcesPath.add("/node_modules/**");

        registry.addInterceptor(notificationInterceptor)
                .excludePathPatterns(staticResourcesPath);
    }
}
