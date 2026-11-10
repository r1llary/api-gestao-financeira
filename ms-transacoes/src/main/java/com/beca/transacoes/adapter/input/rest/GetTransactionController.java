
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.rest.dto.TransactionResponse;
import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.cambio.model.ConversionResult;
import com.beca.transacoes.core.cambio.service.ExchangeConversionService;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.GetTransactionUseCase;
import com.beca.transacoes.core.transacao.port.input.ListTransactionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class GetTransactionController {

    private final GetTransactionUseCase useCase;
    private final ListTransactionsUseCase listUseCase;
    private final ExchangeConversionService conversionService;

    public GetTransactionController(
            GetTransactionUseCase useCase,
            ListTransactionsUseCase listUseCase,
            ExchangeConversionService conversionService) {

        this.useCase = useCase;
        this.listUseCase = listUseCase;
        this.conversionService = conversionService;
    }

    @Operation(
            summary = "Consultar transação por ID",
            description = "Consulta uma transação do usuário autenticado. "
                    + "Retorna os dados da transação e a conversão "
                    + "do valor em dólar e euro, quando disponível."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transação encontrada"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não permitido"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada"),
            @ApiResponse(responseCode = "503", description = "Serviço de autenticação indisponível")
    })
    @GetMapping("/{id}")
    public TransactionConversionResponse getById(
            @Parameter(description = "Identificador da transação")
            @PathVariable Long id,
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthenticatedUser user) {

        Transaction transaction = useCase.getById(id, user.id());

        LocalDate date = transaction.getOccurredAt()
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toLocalDate();

        ConversionResult usd = conversionService.convert(
                transaction.getAmount(), "USD", date
        );

        ConversionResult eur = conversionService.convert(
                transaction.getAmount(), "EUR", date
        );

        return new TransactionConversionResponse(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getOccurredAt(),
                transaction.getDescription(),
                transaction.getCategory(),
                transaction.getStatus(),
                transaction.getDestinationUserId(),
                transaction.getRejectionReason(),
                "BRL",
                usd,
                eur
        );
    }

    @Operation(
            summary = "Listar transações",
            description = "Lista as transações do usuário autenticado. "
                    + "Permite filtrar por data inicial, data final e tipo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transações consultadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Período ou filtro inválido"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "503", description = "Serviço de autenticação indisponível")
    })
    @GetMapping
    public List<TransactionResponse> list(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthenticatedUser user,

            @Parameter(description = "Data inicial no formato AAAA-MM-DD")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "Data final no formato AAAA-MM-DD")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @Parameter(description = "Tipo da transação")
            @RequestParam(required = false)
            TransactionType type) {

        // Busca as transacoes do usuario
        List<Transaction> transactions = listUseCase.list(
                user.id(),
                startDate,
                endDate,
                type
        );

        // Cria uma lista de DTOs
        List<TransactionResponse> responses = new ArrayList<>();

        // Converte cada transacao em um DTO
        for (Transaction transaction : transactions) {

            TransactionResponse response = new TransactionResponse(
                    transaction.getId(),
                    transaction.getUserId(),
                    transaction.getAmount(),
                    transaction.getType(),
                    transaction.getOccurredAt(),
                    transaction.getDescription(),
                    transaction.getCategory(),
                    transaction.getStatus(),
                    transaction.getDestinationUserId(),
                    transaction.getRejectionReason()
            );

            responses.add(response);
        }

        return responses;
    }
}
