package com.beca.transacoes.adapter.input.security;

import com.beca.transacoes.adapter.output.client.UserAuthenticationClient;
import com.beca.transacoes.adapter.output.client.dto.UserAuthenticationResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UserAuthenticationClient client;

    public UserAuthenticationProvider(UserAuthenticationClient client) {
        this.client = client;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String email = authentication.getName();

        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("Password is required");
        }

        String password = authentication.getCredentials().toString();

        UserAuthenticationResponse response =
                client.authenticate(email, password);

        AuthenticatedUser user =
                new AuthenticatedUser(response.id(), response.email());

        return UsernamePasswordAuthenticationToken.authenticated(
                user,
                null,
                List.of()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}