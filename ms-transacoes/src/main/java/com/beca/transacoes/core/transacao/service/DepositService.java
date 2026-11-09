
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.DepositUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;

import java.math.BigDecimal;
import java.time.Instant;

public class DepositService implements DepositUseCase {

    private final TransactionRepositoryPort repository;
    private final AccountGatewayPort accountGateway;

    public DepositService(
            TransactionRepositoryPort repository,
            AccountGatewayPort accountGateway
    ) {
        this.repository = repository;
        this.accountGateway = accountGateway;
    }

    @Override
    public Transaction deposit(
            Long userId,
            BigDecimal amount,
            String description,
            String category
    ) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Account account = accountGateway.findByUserId(userId);

        BigDecimal newBalance = account.balance().add(amount);

        accountGateway.updateBalance(userId, newBalance);

        Transaction transaction = new Transaction(
                null,
                userId,
                amount,
                TransactionType.DEPOSIT,
                Instant.now(),
                description,
                category,
                TransactionStatus.APPROVED,
                null,
                null
        );

        return repository.save(transaction);
    }
}
