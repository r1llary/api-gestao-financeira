package com.beca.usuarios.adapter.input.rest.dto;

import java.util.List;

public record UserImportResponseDTO (
        int imported,
        int failed,
        List<String> errors
) {
}
