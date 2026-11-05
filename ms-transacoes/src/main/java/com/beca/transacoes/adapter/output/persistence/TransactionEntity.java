package com.beca.transacoes.adapter.output.persistence;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Instant occurredAt;
    private String description;
    private String category;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private Long destinationUserId;
    private String rejectionReason;

    protected TransactionEntity() {
    }

    static TransactionEntity fromDomain(Transaction transaction){
        TransactionEntity entity = new TransactionEntity();
        entity.id = transaction.getId();
        entity.userId = transaction.getUserId();
        entity.amount = transaction.getAmount();
        entity.type = transaction.getType();
        entity.occurredAt = transaction.getOccurredAt();
        entity.description = transaction.getDescription();
        entity.category = transaction.getCategory();
        entity.status = transaction.getStatus();
        entity.destinationUserId = transaction.getDestinationUserId();
        entity.rejectionReason =transaction.getRejectionReason();
        return entity;
    }

    Transaction toDomain(){
        return new Transaction(
                id,
                userId,
                amount,
                type,
                occurredAt,
                description,
                category,
                status,
                destinationUserId,
                rejectionReason
        );
    }
}