package com.beca.usuarios.adapter.input.rest;

import com.beca.usuarios.adapter.input.csv.UserCsvImporter;
import com.beca.usuarios.adapter.input.rest.dto.UserRequestDTO;
import com.beca.usuarios.core.usuario.model.User;
import com.beca.usuarios.core.usuario.port.input.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.access.AccessDeniedException;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserUseCase userUseCase;
    private UserController controller;

    @BeforeEach
    void setUp(){
    userUseCase = mock(UserUseCase.class);

        UserMapper userMapper = mock(UserMapper.class);
        PasswordEncoder passwordEncoder =mock(PasswordEncoder.class);
        UserCsvImporter userCsvImporter =mock(UserCsvImporter.class);

        controller = new UserController(
                userUseCase,
                userMapper,
                passwordEncoder,
                userCsvImporter
        );
    }

    @Test
    void shouldRejectDeleteWhenUserIsNotOwner(){
        //Arrange
        User existingUser = new User(
                1L,
                "Ana",
                "ana@example.com",
                "test-password"
        );

        when(userUseCase.findById(1L)).thenReturn(existingUser);

        Principal principal =mock(Principal.class);
        when(principal.getName()).thenReturn("outro@example.com");

        assertThrows(
                AccessDeniedException.class,
                () -> controller.delete(1L, principal)
        );
        verify(userUseCase, never()).deleteById(anyLong());
    }

    @Test
    void shouldDeleteWhenUserIsOwner(){
        User existingUser = new User(
                1L,
                "Ana",
                "ana@example.com",
                "test-password"
        );

        when(userUseCase.findById(1L)).thenReturn(existingUser);

        Principal principal =mock(Principal.class);
        when(principal.getName()).thenReturn("ana@example.com");

        //Act
        ResponseEntity<Void> response = controller.delete(1L, principal);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userUseCase).deleteById(1L);
    }

    @Test
    void shouldRejectUpdateWhenUserIsNotOwner(){
        //Arrange
        User existingUser = new User(
                1L,
                "Ana",
                "ana@example.com",
                "test-password"
        );

        when(userUseCase.findById(1L)).thenReturn(existingUser);

        Principal principal =mock(Principal.class);
        when(principal.getName()).thenReturn("outro@example.com");

        UserRequestDTO request =mock(UserRequestDTO.class);

        assertThrows(
                AccessDeniedException.class,
                () -> controller.update(1L, request, principal)
        );

        verify(userUseCase, never()).update(anyLong(), any(User.class));
    }
}