package com.beca.usuarios.adapter.input.csv;

import com.beca.usuarios.adapter.input.rest.dto.UserImportResponseDTO;
import com.beca.usuarios.adapter.input.rest.dto.UserRequestDTO;
import com.beca.usuarios.core.usuario.exception.EmailAlreadyExistsException;
import com.beca.usuarios.core.usuario.model.User;
import com.beca.usuarios.core.usuario.port.input.UserUseCase;
import jakarta.validation.Validator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.*;
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

    public UserImportResponseDTO read(InputStream input) throws IOException {
        int imported = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {

            // Remove o BOM apenas se ele estiver no início do arquivo.
            reader.mark(1);

            if (reader.read() != '\uFEFF') {
                reader.reset();
            }

            try (CSVParser parser = CSVFormat.RFC4180.parse(reader)) {
                List<CSVRecord> records;

                try {
                    records = parser.getRecords();
                } catch (UncheckedIOException exception) {
                    throw new IllegalArgumentException(
                            "Invalid CSV file: could not read CSV records",
                            exception
                    );
                }

                if (records.isEmpty()) {
                    throw new IllegalArgumentException("CSV file is empty");
                }

                CSVRecord header = records.get(0);

                if (header.size() != 3
                        || !"name".equals(header.get(0))
                        || !"email".equals(header.get(1))
                        || !"password".equals(header.get(2))) {

                    throw new IllegalArgumentException(
                            "Invalid CSV header. Expected: name,email,password"
                    );
                }

                for (int index = 1; index < records.size(); index++) {
                    CSVRecord record = records.get(index);

                    try {
                        if (record.size() != 3) {
                            throw new IllegalArgumentException(
                                    "Expected 3 fields"
                            );
                        }

                        UserRequestDTO request = new UserRequestDTO(
                                record.get(0),
                                record.get(1),
                                record.get(2)
                        );

                        if (!validator.validate(request).isEmpty()) {
                            throw new IllegalArgumentException(
                                    "Invalid user data"
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

                    } catch (IllegalArgumentException
                             | EmailAlreadyExistsException exception) {

                        errors.add(
                                "Record " + record.getRecordNumber()
                                        + ": " + exception.getMessage()
                        );
                    }
                }
            }
        }

        return new UserImportResponseDTO(imported, errors.size(), errors);
    }
}
