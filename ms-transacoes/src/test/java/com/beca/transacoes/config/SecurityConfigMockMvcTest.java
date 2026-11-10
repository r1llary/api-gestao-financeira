
package com.beca.transacoes.config;

import com.beca.transacoes.adapter.input.rest.AuthenticationController;
import com.beca.transacoes.adapter.input.security.UserAuthenticationProvider;
import com.beca.transacoes.adapter.output.client.UserAuthenticationClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(AuthenticationController.class)
@Import({
        SecurityConfig.class,
        UserAuthenticationProvider.class
})
class SecurityConfigMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAuthenticationClient client;

    @Test
    void shouldReturn401WithoutCredentials() throws Exception {

        // Arrange
        // Requisicao sem credenciais.

        // Act
        MvcResult result = mockMvc.perform(
                get("/auth/me")
        ).andReturn();

        // Assert
        assertEquals(401, result.getResponse().getStatus());
    }

    @Test
    void shouldReturn503WhenUserServiceIsUnavailable() throws Exception {

        // Arrange
        when(client.authenticate(anyString(), anyString()))
                .thenThrow(new AuthenticationServiceException(
                        "MS1 unavailable"
                ));

        // Act
        MvcResult result = mockMvc.perform(
                get("/auth/me")
                        .with(httpBasic("teste@email.com", "123456"))
        ).andReturn();

        // Assert
        assertEquals(503, result.getResponse().getStatus());
    }
}
