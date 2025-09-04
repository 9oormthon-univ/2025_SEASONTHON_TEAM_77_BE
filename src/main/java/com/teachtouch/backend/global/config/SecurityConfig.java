package com.teachtouch.backend.global.config;

import com.teachtouch.backend.global.jwt.JwtAuthenticationFilter;
import com.teachtouch.backend.global.jwt.JwtProvider;
import com.teachtouch.backend.global.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(
                                "/api/v1.0/user/signup",
                                "api/v1.0/user/login",
                                "api/v1.0/user/check-id",
                                "api/v1.0/user/reissue",
                                "/api/v1.0/products",
                                "/api/v1.0/products/batch",
                                "/api/v1.0/examples",
                                "/api/v1.0/guides",
                                "/api/v1.0/guides/{id}",
                                "/api/v1.0/guides/search",
                                "/oauth2/**",
                                "/api/v1.0/tts",
                                "/api/v1.0/retouch/test/all",
                                "/api/v1.0/retouch/test/add",
                                "/api/v1.0/retouch/test/**"

                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider,customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception{
        return config.getAuthenticationManager();
    }

}
