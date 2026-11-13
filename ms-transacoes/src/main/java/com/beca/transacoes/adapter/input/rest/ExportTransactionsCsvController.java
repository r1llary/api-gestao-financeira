
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.port.input.ExportTransactionsCsvUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/transactions")
@Tag(name = "CSV Export")
public class ExportTransactionsCsvController {

    private final ExportTransactionsCsvUseCase useCase;

    public ExportTransactionsCsvController(
            ExportTransactionsCsvUseCase useCase) {
        this.useCase = useCase;
    }

    @Operation(
            summary = "Export transactions to CSV",
            description = "Generates a CSV file containing transactions "
                    + "belonging to the authenticated user, "
                    + "with optional date range filtering."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CSV file generated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date range"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Authentication service unavailable"
            )
    })
    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthenticatedUser user,

            @Parameter(description = "Start date in YYYY-MM-DD format")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "End date in YYYY-MM-DD format")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        byte[] csv = useCase.export(
                user.id(),
                startDate,
                endDate
        );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"transactions.csv\""
                )
                .contentType(new MediaType(
                        "text", "csv", StandardCharsets.UTF_8
                ))
                .body(csv);
    }
}
