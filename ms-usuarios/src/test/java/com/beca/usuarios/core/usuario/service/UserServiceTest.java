package com.beca.usuarios.core.usuario.service;

import com.beca.usuarios.core.usuario.exception.EmailAlreadyExistsException;
import com.beca.usuarios.core.usuario.exception.UserNotFoundException;
import com.beca.usuarios.core.usuario.model.User;
import com.beca.usuarios.core.usuario.port.output.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepositoryPort repository;
    private UserService service;

    @BeforeEach
    void setUp(){
        repository = mock(UserRepositoryPort.class);
        service = new UserService(repository);
    }
    @Test
    void shouldRejectUserWhenEmailAlreadyExists(){

        User user = new User(
                null,
                "Ana",
                "ana@example.com",
                "test-password"
        );

        when(repository.existsByEmail("ana@example.com"))
                .thenReturn(true);

        //Act
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> service.create(user)
        );

        //Assert
        assertEquals("Email already registered", exception.getMessage());
        verify(repository, never()).save(any(User.class));

    }

    @Test
    void shouldCreateUserWhenEmailDoesNotExist(){

        User user = new User(
                null,
                "Ana",
                "ana@example.com",
                "test-password"
        );
        User savedUser = new User(
                1L,
                "Ana",
                "ana@example.com",
                "test-password"
        );

        when(repository.existsByEmail("ana@example.com"))
                .thenReturn(false);

        when(repository.save(user)).thenReturn(savedUser);

        //Act
        User result = service.create(user);

        //Assert
        assertEquals(1L, result.getId());
        assertEquals("Ana", result.getName());
        assertEquals("ana@example.com", result.getEmail());
        verify(repository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFound(){

        when(repository.findById(99L)).thenReturn(Optional.empty());

        //Act
        assertThrows(
                UserNotFoundException.class,
                () -> service.findById(99L)
        );

        //Assert
        verify(repository).findById(99L);
    }

    @Test
    void shouldReturnUserWhenIdExists(){

        User user = new User(
                1L,
                "Ana",
                "ana@example.com",
                "test-password"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        //Act
        User result = service.findById(1L);

        //Assert
        assertEquals(1L, result.getId());
        assertEquals("Ana", result.getName());
        assertEquals("ana@example.com", result.getEmail());
        verify(repository).findById(1L);

    }

    @Test
    void shouldNotDeleteUserWhenIdDoesNotExist(){

        when(repository.findById(99L)).thenReturn(Optional.empty());


        assertThrows(
                UserNotFoundException.class,
                () -> service.deleteById(99L)
        );

        //Assert
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void shouldDeleteUserWhenIdExists(){
        //Arrange
        User user = new User(
                1L,
                "Ana",
                "ana@example.com",
                "test-password"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        //Act
        service.deleteById(1L);

        //Assert
        verify(repository).deleteById(1L);
    }

    @Test
    void shouldReturnAllUsers(){
        //Arrange
        User user = new User(
                1L,
                "Ana",
                "ana@example.com",
                "test-password"
        );

        when(repository.findAll()).thenReturn(List.of(user));

        //Act
        List<User> result = service.findAll();

        //Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Ana", result.get(0).getName());
        assertEquals("ana@example.com", result.get(0).getEmail());
        verify(repository).findAll();
    }

    @Test
    void sholdRejectUpdateWhenEmailAlreadyExists(){
        // Arrange
        User existingUser = new User(
                1L,
                "Ana",
                "ana@example.com",
                "test-password"
        );

        User updatedUser = new User(
                null,
                "Ana",
                "other@example.com",
                "test-password"
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(repository.existsByEmail("other@example.com"))
                .thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> service.update(1L, updatedUser)
        );

        // Assert
        assertEquals("Email already registered", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmailAfterNormalization(){
        User user = new User(
                null,
                "Ana",
                " ANA@EXAMPLE.COM ",
                "test-password"
        );

        when(repository.existsByEmail("ana@example.com"))
                .thenReturn(true);

        assertEquals("ana@example.com", user.getEmail());

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> service.create(user)
        );

        verify(repository).existsByEmail("ana@example.com");
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        // Arrange
        User existingUser = new User(
                1L,
                "Ana",
                "ana@example.com",
                "old-password-hash"
        );

        User requestedUser = new User(
                null,
                "Ana Atualizada",
                "novo@example.com",
                "new-password-hash"
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(repository.existsByEmail("novo@example.com"))
                .thenReturn(false);

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = service.update(1L, requestedUser);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Ana Atualizada", result.getName());
        assertEquals("novo@example.com", result.getEmail());
        assertEquals("new-password-hash", result.getPasswordHash());

        verify(repository).existsByEmail("novo@example.com");
        verify(repository).save(result);
    }
}
