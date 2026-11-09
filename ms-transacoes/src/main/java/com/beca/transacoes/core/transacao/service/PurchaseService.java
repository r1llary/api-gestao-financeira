
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.PurchaseUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import com.beca.transacoes.core.conta.model.Account;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

public class PurchaseService implements PurchaseUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionValidationService validationService;
    private final AccountGatewayPort accountGateway;

    public PurchaseService(
            TransactionRepositoryPort repository,
            TransactionValidationService validationService,
            AccountGatewayPort accountGateway) {

        this.repository = repository;
        this.validationService = validationService;
        this.accountGateway = accountGateway;
    }

    @Override
    @Transactional
    public Transaction purchase(
            Long userId,
            BigDecimal amount,
            String description,
            String category) {

        Instant now = Instant.now();

        String reason = validationService.validate(userId, amount, now);

        TransactionStatus status = TransactionStatus.APPROVED;

        if (reason != null) {
            status = TransactionStatus.REJECTED;
        }

        Transaction transaction = new Transaction(
                null,
                userId,
                amount,
                TransactionType.PURCHASE,
                now,
                description,
                category,
                status,
                null,
                reason
        );

        // Salvar a transação
        Transaction savedTransaction = repository.save(transaction);

        if (status == TransactionStatus.APPROVED) {

            // Consultar a conta do usuário
            Account account = accountGateway.findByUserId(userId);

            // Calcular o novo saldo
            BigDecimal newBalance = account.balance().subtract(amount);

            // Descontar o valor da compra
            accountGateway.updateBalance(userId, newBalance);
        }

        return savedTransaction;
    }

}
