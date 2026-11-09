
package com.beca.transacoes.adapter.output.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BrasilApiExchangeResponse(
        String moeda,
        LocalDate data,
        List<Quote> cotacoes
) {

    public record Quote(
            @JsonProperty("cotacao_venda")
            BigDecimal cotacaoVenda,

            @JsonProperty("tipo_boletim")
            String tipoBoletim
    ) {
    }
}
