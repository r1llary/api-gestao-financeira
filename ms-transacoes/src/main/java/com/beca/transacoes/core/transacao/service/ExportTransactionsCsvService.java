
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.port.input.ExportTransactionsCsvUseCase;
import com.beca.transacoes.core.transacao.port.input.ListTransactionsUseCase;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.math.RoundingMode;
import java.util.List;

public class ExportTransactionsCsvService
        implements ExportTransactionsCsvUseCase {

    private final ListTransactionsUseCase listUseCase;

    public ExportTransactionsCsvService(
            ListTransactionsUseCase listUseCase) {
        this.listUseCase = listUseCase;
    }

    @Override
    public byte[] export(
            Long userId,
            LocalDate startDate,
            LocalDate endDate) {

        List<Transaction> transactions = listUseCase.list(
                userId, startDate, endDate, null
        );

        StringBuilder csv = new StringBuilder();

        csv.append("Data;Tipo;Categoria;Descrição;Valor (BRL);Status;Motivo da rejeição\r\n");

        for (Transaction transaction : transactions) {

            String date = transaction.getOccurredAt()
                    .atZone(ZoneId.of("America/Sao_Paulo"))
                    .toLocalDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String amount = transaction.getAmount()
                    .setScale(2, RoundingMode.HALF_UP)
                    .toPlainString()
                    .replace('.', ',');

            csv.append(date).append(";")
                    .append(transaction.getType()).append(";")
                    .append(escape(transaction.getCategory())).append(";")
                    .append(escape(transaction.getDescription())).append(";")
                    .append(amount).append(";")
                    .append(transaction.getStatus()).append(";")
                    .append(escape(transaction.getRejectionReason()))
                    .append("\r\n");
        }

        return ("\uFEFF" + csv)
                .getBytes(StandardCharsets.UTF_8);
    }

    private String escape(String value) {

        if (value == null) {
            return "";
        }

        // Evita que o Excel interprete textos como fórmulas.
        String check = value.stripLeading();

        if (check.startsWith("=")
                || check.startsWith("+")
                || check.startsWith("-")
                || check.startsWith("@")) {
            value = "'" + value;
        }

        String result = value.replace("\"", "\"\"");

        if (result.contains(";")
                || result.contains("\"")
                || result.contains("\n")
                || result.contains("\r")) {

            return "\"" + result + "\"";
        }

        return result;
    }
}
