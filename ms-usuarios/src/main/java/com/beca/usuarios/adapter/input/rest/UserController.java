package com.beca.usuarios.adapter.input.rest;

import com.beca.usuarios.adapter.input.rest.dto.UserRequestDTO;
import com.beca.usuarios.adapter.input.rest.dto.UserResponseDTO;
import com.beca.usuarios.core.usuario.model.User;
import com.beca.usuarios.core.usuario.port.input.UserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserUseCase userUseCase, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userUseCase = userUseCase;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(
            @Valid @RequestBody UserRequestDTO request
            ){
        User user = userMapper.toDomain(request, passwordEncoder);
        User createdUser = userUseCase.create(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toResponse(createdUser));
    }
}
