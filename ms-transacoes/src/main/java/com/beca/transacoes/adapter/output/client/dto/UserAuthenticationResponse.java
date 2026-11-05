package com.beca.transacoes.adapter.output.client.dto;

public record UserAuthenticationResponse(
        Long id,
        String name,
        String email
) {
}
