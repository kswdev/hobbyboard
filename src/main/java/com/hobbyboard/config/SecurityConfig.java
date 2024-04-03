package com.hobbyboard.config;

import com.hobbyboard.domain.account.dto.AccountDto;
import com.hobbyboard.domain.account.dto.security.UserAccount;
import com.hobbyboard.domain.account.entity.Account;
import com.hobbyboard.domain.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.sql.DataSource;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class SecurityConfig {
    private final DataSource dataSource;

    @Bean
    public SecurityFilterChain securityFilterChain(
            AccountService accountService,
            ModelMapper modelMapper,
            HttpSecurity http
    ) throws Exception {

        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/",
                                "/login",
                                "/sign-up",
                                "/check-email",
                                "/check-email-token",
                                "/email-login",
                                "/check-email-login",
                                "/login-link",
                                "/node_modules/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(
                        login -> login.loginPage("/login").permitAll())
                .logout(
                        logout -> logout.logoutSuccessUrl("/"))
                .rememberMe(
                        config -> config
                                .userDetailsService(userDetailsService(accountService, modelMapper))
                                .tokenRepository(tokenRepository()))
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .build();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);

        return jdbcTokenRepository;
    }
    @Bean
    public UserDetailsService userDetailsService(
            AccountService accountService,
            ModelMapper modelMapper
    ) {
        return username -> {
            Account account = accountService
                    .findByEmail(username)
                    .orElseThrow(() -> new NoSuchElementException("없는 아이디입니다."));

            log.info("accountEntity : {}", account);

            AccountDto accountDto = AccountDto.fromAccount(account);

            log.info("accountDto : {}", accountDto);
            return new UserAccount(accountDto);
        };
    }

    @Bean
    public SecurityContextHolderStrategy securityContextHolderStrategy() {
        return SecurityContextHolder.getContextHolderStrategy();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
