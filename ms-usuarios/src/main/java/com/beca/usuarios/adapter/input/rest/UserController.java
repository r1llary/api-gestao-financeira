package com.beca.usuarios.adapter.input.rest;

import com.beca.usuarios.adapter.input.csv.UserCsvImporter;
import com.beca.usuarios.adapter.input.rest.dto.UserImportResponseDTO;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserCsvImporter userCsvImporter;

    public UserController(UserUseCase userUseCase, UserMapper userMapper, PasswordEncoder passwordEncoder, UserCsvImporter userCsvImporter) {
        this.userUseCase = userUseCase;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userCsvImporter = userCsvImporter;
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

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<UserImportResponseDTO> importUsers(
            @RequestParam("file")MultipartFile file) throws IOException{
        if(file.isEmpty()){
            throw new IllegalArgumentException("CSV file is empty");
        }

        UserImportResponseDTO response =
                userCsvImporter.read(file.getInputStream());

        return ResponseEntity.ok(response);
    }
}
