
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.ListTransactionsUseCase;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExportTransactionsCsvServiceTest {

    @Test
    void shouldExportTransactionAsCsv() {

        // Arrange
        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        ExportTransactionsCsvService service =
                new ExportTransactionsCsvService(listUseCase);

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        Transaction transaction = new Transaction(
                1L,
                6L,
                new BigDecimal("50.30"),
                TransactionType.PURCHASE,
                Instant.parse("2026-11-06T14:00:00Z"),
                "Almoço",
                "Food",
                TransactionStatus.APPROVED,
                null,
                null
        );

        when(listUseCase.list(6L, start, end, null))
                .thenReturn(List.of(transaction));

        // Act
        byte[] result = service.export(6L, start, end);

        String csv = new String(result, StandardCharsets.UTF_8);

        // Assert
        assertTrue(csv.startsWith("\uFEFF"));

        assertTrue(csv.contains(
                "Data;Tipo;Categoria;Descrição;Valor (BRL);Status;Motivo da rejeição"
        ));

        assertTrue(csv.contains(
                "06/11/2026;PURCHASE;Food;Almoço;50,30;APPROVED;"
        ));

        verify(listUseCase).list(6L, start, end, null);
    }

    @Test
    void shouldExportRejectedTransactionAsCsv() {

        // Arrange
        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        ExportTransactionsCsvService service =
                new ExportTransactionsCsvService(listUseCase);

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        Transaction transaction = new Transaction(
                2L,
                6L,
                new BigDecimal("150.00"),
                TransactionType.PURCHASE,
                Instant.parse("2026-11-07T14:00:00Z"),
                "Compra no mercado",
                "Food",
                TransactionStatus.REJECTED,
                null,
                "Insufficient balance"
        );

        when(listUseCase.list(6L, start, end, null))
                .thenReturn(List.of(transaction));

        // Act
        byte[] result = service.export(6L, start, end);

        String csv = new String(
                result, StandardCharsets.UTF_8
        );

        // Assert
        assertTrue(csv.contains(
                "07/11/2026;PURCHASE;Food;Compra no mercado;150,00;REJECTED;Insufficient balance"
        ));

        verify(listUseCase).list(6L, start, end, null);
    }


    @Test
    void shouldEscapeSpecialCharactersInCsv() {

        // Arrange
        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        ExportTransactionsCsvService service =
                new ExportTransactionsCsvService(listUseCase);

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        Transaction transaction = new Transaction(
                3L,
                6L,
                new BigDecimal("45.00"),
                TransactionType.PURCHASE,
                Instant.parse("2026-11-08T14:00:00Z"),
                "Café \"especial\"; com pão\nsegunda linha",
                "Food; Drinks",
                TransactionStatus.APPROVED,
                null,
                null
        );

        when(listUseCase.list(6L, start, end, null))
                .thenReturn(List.of(transaction));

        // Act
        byte[] result = service.export(6L, start, end);

        String csv = new String(result, StandardCharsets.UTF_8);

        // Assert
        assertTrue(csv.contains("\"Food; Drinks\""));

        assertTrue(csv.contains(
                "\"Café \"\"especial\"\"; com pão\nsegunda linha\""
        ));

        verify(listUseCase).list(6L, start, end, null);
    }

    @Test
    void shouldExportOnlyHeaderWhenNoTransactionsExist() {

        // Arrange
        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        ExportTransactionsCsvService service =
                new ExportTransactionsCsvService(listUseCase);

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        when(listUseCase.list(6L, start, end, null))
                .thenReturn(List.of());

        // Act
        byte[] result = service.export(6L, start, end);

        String csv = new String(
                result, StandardCharsets.UTF_8
        );

        // Assert
        assertEquals(
                "\uFEFFData;Tipo;Categoria;Descrição;Valor (BRL);Status;Motivo da rejeição\r\n",
                csv
        );

        verify(listUseCase).list(6L, start, end, null);
    }


}
