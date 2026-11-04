package com.beca.transacoes.core.transacao.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {


    private final Long id;
    private final Long userId;
    private final BigDecimal amount;
    private final TransactionType type;
    private final Instant occurredAt;
    private final String description;
    private final String category;
    private final TransactionStatus status;
    private final Long destinationUserId;
    private final String rejectionReason;

    public Transaction(Long id, Long userId, BigDecimal amount, TransactionType type, Instant occurredAt, String description, String category, TransactionStatus status, Long destinationUserId, String rejectionReason) {

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
            throw new IllegalArgumentException("Transaction date is required");
        }
        if (description == null || description.isBlank()){
            throw new IllegalArgumentException("Description is required");
        }
        if (category == null || category.isBlank()){
            throw new IllegalArgumentException("Category is required");
        }
        if (status == null){
            throw new IllegalArgumentException("Transaction status is required");
        }
        if (type == TransactionType.TRANSFER && destinationUserId == null){
            throw new IllegalArgumentException("Destination user ID is required for transfers");
        }
        if (status == TransactionStatus.REJECTED && (rejectionReason == null || rejectionReason.isBlank())){
            throw new IllegalArgumentException("Rejection reason is required for rejected transactions");
        }


        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.occurredAt = occurredAt;
        this.description = description;
        this.category = category;
        this.status = status;
        this.destinationUserId = destinationUserId;
        this.rejectionReason = rejectionReason;
    }


    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Long getDestinationUserId() {
        return destinationUserId;
    }

    public String getRejectionReason() {
        return rejectionReason;
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
