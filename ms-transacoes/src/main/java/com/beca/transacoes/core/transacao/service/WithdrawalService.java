
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.input.WithdrawalUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

public class WithdrawalService implements WithdrawalUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionValidationService validationService;
    private final AccountGatewayPort accountGateway;

    public WithdrawalService(
            TransactionRepositoryPort repository,
            TransactionValidationService validationService,
            AccountGatewayPort accountGateway
    ) {
        this.repository = repository;
        this.validationService = validationService;
        this.accountGateway = accountGateway;
    }

    @Override
    @Transactional
    public Transaction withdraw(
            Long userId,
            BigDecimal amount,
            String description,
            String category
    ) {
        Instant date = Instant.now();

        String reason = validationService.validate(userId, amount, date);

        TransactionStatus status = TransactionStatus.APPROVED;

        if (reason != null) {
            status = TransactionStatus.REJECTED;
        }

        Transaction transaction = new Transaction(
                null,
                userId,
                amount,
                TransactionType.WITHDRAWAL,
                date,
                description,
                category,
                status,
                null,
                reason
        );

        Transaction savedTransaction = repository.save(transaction);

        if (status == TransactionStatus.APPROVED) {
            Account account = accountGateway.findByUserId(userId);

            BigDecimal newBalance = account.balance().subtract(amount);

            accountGateway.updateBalance(userId, newBalance);
        }

        return savedTransaction;
    }
}
