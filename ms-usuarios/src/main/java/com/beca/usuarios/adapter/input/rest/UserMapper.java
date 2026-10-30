package com.beca.usuarios.adapter.input.rest;

import com.beca.usuarios.adapter.input.rest.dto.UserRequestDTO;
import com.beca.usuarios.adapter.input.rest.dto.UserResponseDTO;
import com.beca.usuarios.core.usuario.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toResponse(User user){
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User toDomain(UserRequestDTO request, PasswordEncoder encoder){
        String passwordHash = encoder.encode(request.password());
        return new User(
                null,
                request.name(),
                request.email(),
                passwordHash
        );
    }

}
