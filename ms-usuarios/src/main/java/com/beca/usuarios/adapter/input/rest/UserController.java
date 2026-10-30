package com.beca.usuarios.adapter.input.rest;

import com.beca.usuarios.adapter.input.rest.dto.UserRequestDTO;
import com.beca.usuarios.adapter.input.rest.dto.UserResponseDTO;
import com.beca.usuarios.core.usuario.model.User;
import com.beca.usuarios.core.usuario.port.input.UserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List <UserResponseDTO>> findAll(){
        List<User> users = userUseCase.findAll();

        List<UserResponseDTO> response = new ArrayList<>();

        for (User user : users){
            response.add(userMapper.toResponse(user));
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id){
        User user = userUseCase.findById(id);

        UserResponseDTO response = userMapper.toResponse(user);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO request){

        User user = userMapper.toDomain(request, passwordEncoder);
        User updatedUser = userUseCase.update(id, user);

        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userUseCase.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
