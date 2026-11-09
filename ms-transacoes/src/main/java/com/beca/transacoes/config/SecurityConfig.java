package com.beca.transacoes.config;

import com.beca.transacoes.adapter.input.security.UserAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserAuthenticationProvider provider
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(provider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> basic
                        .authenticationEntryPoint((request, response, exception) -> {
                            response.setContentType("text/plain;charset=UTF-8");

                            if (exception instanceof AuthenticationServiceException){
                                response.setStatus(503);
                                response.getWriter().write(
                                        "Authentication service unavailable"
                                );
                                return;
                            }

                            response.setHeader("WWW-Authenticate", "Basic realm=\"ms-transacoes\"");
                            response.setStatus(401);
                            response.getWriter().write(
                                    "Missing or invalid credentials"
                            );
                        })
                        );

        return http.build();
    }
}