package com.beca.usuarios.adapter.input.csv;

import com.beca.usuarios.adapter.input.rest.dto.UserImportResponseDTO;
import com.beca.usuarios.adapter.input.rest.dto.UserRequestDTO;
import com.beca.usuarios.core.usuario.model.User;
import com.beca.usuarios.core.usuario.port.input.UserUseCase;
import jakarta.validation.Validator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserCsvImporter {

    private final Validator validator;
    private final UserUseCase userUseCase;
    private final PasswordEncoder passwordEncoder;

    public UserCsvImporter(Validator validator, UserUseCase userUseCase, PasswordEncoder passwordEncoder) {
        this.validator = validator;
        this.userUseCase = userUseCase;
        this.passwordEncoder = passwordEncoder;
    }

    public UserImportResponseDTO read(InputStream input) throws IOException{

        int imported = 0;
        List<String> errors = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))){

            String header = reader.readLine();

            if (!"name,email,password".equals(header)){
                throw new IllegalArgumentException(
                        "Invalid CSV header. Expected: name,email,password"
                );
            }

            String line = reader.readLine();
            int lineNumber = 2;

            while (line != null){

                try {
                    String[] fields = line.split(",", -1);

                if (fields.length != 3){
                    throw new IllegalArgumentException(
                            "Invalid CSV format at line " + lineNumber
                    );
                }

                String name = fields[0];
                String email = fields[1];
                String password = fields[2];

                UserRequestDTO request = new UserRequestDTO(name, email, password);

                if(!validator.validate(request).isEmpty()){
                    throw new IllegalArgumentException(
                            "Invalid user dataat line " + lineNumber
                    );
                }

                User user = new User(
                        null,
                        request.name(),
                        request.email(),
                        passwordEncoder.encode(request.password())
                );

                userUseCase.create(user);
                imported++;

                } catch (IllegalArgumentException e){
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }

                line = reader.readLine();
                lineNumber++;
            }
        }
        return new UserImportResponseDTO(imported, errors.size(), errors);
    }
}
