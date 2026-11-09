package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.TransferUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import com.beca.transacoes.core.transacao.port.output.UserLookupPort;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.conta.model.Account;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;


public class TransferService implements TransferUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionValidationService validationService;
    private final UserLookupPort userLookupPort;
    private final AccountGatewayPort accountGateway;

    public TransferService(
            TransactionRepositoryPort repository,
            TransactionValidationService validationService,
            UserLookupPort userLookupPort,
            AccountGatewayPort accountGateway) {

        this.repository = repository;
        this.validationService = validationService;
        this.userLookupPort = userLookupPort;
        this.accountGateway = accountGateway;
    }


    @Override
    @Transactional
    public Transaction transfer(
            Long userId,
            Long destinationUserId,
            BigDecimal amount,
            String description,
            String category,
            String authorization) {

        Instant now = Instant.now();
        String reason = null;

        if (destinationUserId.equals(userId)) {
            reason = "Cannot transfer to yourself";

        } else if (!userLookupPort.userExists(destinationUserId, authorization)) {
            reason = "Destination user not found";

        } else {
            reason = validationService.validate(userId, amount, now);
        }

        TransactionStatus status = TransactionStatus.APPROVED;

        if (reason != null) {
            status = TransactionStatus.REJECTED;
        }

        Transaction transaction = new Transaction(
                null,
                userId,
                amount,
                TransactionType.TRANSFER,
                now,
                description,
                category,
                status,
                destinationUserId,
                reason
        );

        Transaction savedTransaction = repository.save(transaction);

        if (status == TransactionStatus.APPROVED) {

            // Consultar as duas contas
            Account originAccount = accountGateway.findByUserId(userId);
            Account destinationAccount = accountGateway.findByUserId(destinationUserId);

            // Calcular os novos saldos
            BigDecimal newOriginBalance = originAccount.balance().subtract(amount);
            BigDecimal newDestinationBalance = destinationAccount.balance().add(amount);

            // Atualizar os saldos
            // Descontar o valor da conta de origem
            accountGateway.updateBalance(userId, newOriginBalance);

            try {
                // Adicionar o valor à conta de destino
                accountGateway.updateBalance(destinationUserId, newDestinationBalance);

            } catch (RuntimeException creditError) {

                try {
                    // Tentar devolver o valor à conta de origem
                    accountGateway.updateBalance(userId, originAccount.balance());

                } catch (RuntimeException refundError) {

                    // Guardar o erro da devolução
                    creditError.addSuppressed(refundError);
                }

                // Manter o erro original do crédito
                throw creditError;
            }

        }

        return savedTransaction;
    }

}