package com.beca.transacoes.core.transacao.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {

    private Long userId;
    private BigDecimal amount;
    private TransactionType type;
    private Instant occurredAt;

    public Transaction(Long userId, BigDecimal amount, TransactionType type, Instant occurredAt) {

        if (userId == null){
            throw new IllegalArgumentException("User ID is required");
        }
        if (amount == null || amount.signum() <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (type == null){
            throw new IllegalArgumentException("Transaction type is required");
        }
        if (occurredAt == null){
            throw new IllegalArgumentException("Transaction date and time are required");
        }

        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.occurredAt = occurredAt;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
