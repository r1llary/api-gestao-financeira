package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.input.AccountUseCase;
import com.beca.transacoes.core.conta.service.AccountValidator;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class TransactionValidationService {

    private final AccountUseCase accountUseCase;
    private final TransactionRepositoryPort repository;
    private final AccountValidator validator = new AccountValidator();

    public TransactionValidationService(
            AccountUseCase accountUseCase,
            TransactionRepositoryPort repository
    ) {
        this.accountUseCase = accountUseCase;
        this.repository = repository;
    }

    public String validate(Long userId, BigDecimal amount, Instant date) {

        Account account = accountUseCase.findByUserId(userId);

        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        LocalDate day = date.atZone(zone).toLocalDate();

        Instant start = day.atStartOfDay(zone).toInstant();
        Instant end = day.plusDays(1).atStartOfDay(zone).toInstant();

        List<Transaction> transactions =
                repository.findApprovedByUserIdAndPeriod(
                        userId, start, end
                );

        return validator.validateTransaction(
                account, transactions, amount
        );
    }
}
