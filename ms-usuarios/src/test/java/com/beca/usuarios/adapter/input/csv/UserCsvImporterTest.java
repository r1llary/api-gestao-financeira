package com.beca.usuarios.adapter.input.csv;

import com.beca.usuarios.core.usuario.model.User;
import com.beca.usuarios.core.usuario.port.input.UserUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCsvImporterTest {

    private Validator validator;
    private UserUseCase userUseCase;
    private PasswordEncoder passwordEncoder;
    private UserCsvImporter importer;

    @BeforeEach
    void setUp() {
        validator = mock(Validator.class);
        userUseCase = mock(UserUseCase.class);
        passwordEncoder = mock(PasswordEncoder.class);

        importer = new UserCsvImporter(
                validator,
                userUseCase,
                passwordEncoder
        );
    }

    @Test
    void shouldImportNameWithCommaInsideQuotes() throws Exception {
        String csv = """
                name,email,password
                "Ana, Maria",ana@example.com,Teste@12345
                """;

        when(validator.validate(any())).thenReturn(Set.of());
        when(passwordEncoder.encode("Teste@12345"))
                .thenReturn("encoded-password");

        importer.read(new ByteArrayInputStream(
                csv.getBytes(StandardCharsets.UTF_8)
        ));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userUseCase).create(captor.capture());

        User user = captor.getValue();

        assertEquals("Ana, Maria", user.getName());
        assertEquals("ana@example.com", user.getEmail());
        assertEquals("encoded-password", user.getPasswordHash());
    }

    @Test
    void shouldImportFileWithBom() throws Exception {
        String csv = "\uFEFFname,email,password\n"
                + "Ana,ana@example.com,Teste@12345\n";

        when(validator.validate(any())).thenReturn(Set.of());
        when(passwordEncoder.encode("Teste@12345"))
                .thenReturn("encoded-password");

        importer.read(new ByteArrayInputStream(
                csv.getBytes(StandardCharsets.UTF_8)
        ));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userUseCase).create(captor.capture());

        assertEquals("Ana", captor.getValue().getName());
        assertEquals("ana@example.com", captor.getValue().getEmail());
    }

    @Test
    void shouldRejectInvalidHeader() {
        String csv = """
            nome,email,password
            Ana,ana@example.com,Teste@12345
            """;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.read(new ByteArrayInputStream(
                        csv.getBytes(StandardCharsets.UTF_8)
                ))
        );

        assertEquals(
                "Invalid CSV header. Expected: name,email,password",
                exception.getMessage()
        );

        verifyNoInteractions(userUseCase, passwordEncoder);
    }
}