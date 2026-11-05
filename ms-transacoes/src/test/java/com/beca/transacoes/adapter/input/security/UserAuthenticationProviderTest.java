package com.beca.transacoes.adapter.input.security;

import com.beca.transacoes.adapter.output.client.UserAuthenticationClient;
import com.beca.transacoes.adapter.output.client.dto.UserAuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthenticationProviderTest {

    private final UserAuthenticationClient client =
            mock(UserAuthenticationClient.class);

    private final UserAuthenticationProvider provider =
            new UserAuthenticationProvider(client);

    @Test
    void shouldAuthenticateValidUser() {
        when(client.authenticate("ana@example.com", "test-password"))
                .thenReturn(new UserAuthenticationResponse(
                        1L, "Ana", "ana@example.com"
                ));

        Authentication request =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        "ana@example.com", "test-password"
                );

        Authentication result = provider.authenticate(request);

        AuthenticatedUser user = (AuthenticatedUser) result.getPrincipal();

        assertTrue(result.isAuthenticated());
        assertEquals(1L, user.id());
        assertEquals("ana@example.com", user.email());
        assertNull(result.getCredentials());
    }

    @Test
    void shouldRejectInvalidCredentials() {
        when(client.authenticate("ana@example.com", "wrong-password"))
                .thenThrow(new BadCredentialsException(
                        "Invalid email or password"
                ));

        Authentication request =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        "ana@example.com", "wrong-password"
                );

        assertThrows(
                BadCredentialsException.class,
                () -> provider.authenticate(request)
        );
    }

    @Test
    void shouldFailWhenUserServiceIsUnavailable() {
        when(client.authenticate("ana@example.com", "test-password"))
                .thenThrow(new AuthenticationServiceException(
                        "Unable to contact user service"
                ));

        Authentication request =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        "ana@example.com", "test-password"
                );

        assertThrows(
                AuthenticationServiceException.class,
                () -> provider.authenticate(request)
        );
    }
}