package com.beca.transacoes.core.transacao.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {

    private static final BigDecimal AMOUNT = new BigDecimal("50.00");
    private static final Instant OCCURRED_AT = Instant.parse("2026-11-04T12:00:00Z");

    @Test
    void shouldRejectTransactionWithZeroAmount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        null, 7L, BigDecimal.ZERO, TransactionType.PURCHASE, OCCURRED_AT,
                        "Grocery purchase", "Groceries", TransactionStatus.APPROVED,
                        null, null
                )
        );

        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldCreateWithdrawalWithPositiveAmount() {
        Transaction transaction = new Transaction(
                null, 7L, AMOUNT, TransactionType.WITHDRAWAL, OCCURRED_AT,
                "Cash withdrawal", "Cash", TransactionStatus.APPROVED,
                null, null
        );

        assertEquals(AMOUNT, transaction.getAmount());
        assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
    }

    @Test
    void shouldRejectTransferWithoutDestinationUser() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        null, 7L, AMOUNT, TransactionType.TRANSFER, OCCURRED_AT,
                        "Transfer to another user", "Transfer", TransactionStatus.APPROVED,
                        null, null
                )
        );

        assertEquals(
                "Destination user ID is required for transfers",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectTransactionWithoutStatus() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        null, 7L, AMOUNT, TransactionType.PURCHASE, OCCURRED_AT,
                        "Grocery purchase", "Groceries", null,
                        null, null
                )
        );

        assertEquals("Transaction status is required", exception.getMessage());
    }

    @Test
    void shouldRejectTransactionWithoutCategory() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        null, 7L, AMOUNT, TransactionType.PURCHASE, OCCURRED_AT,
                        "Grocery purchase", null, TransactionStatus.APPROVED,
                        null, null
                )
        );

        assertEquals("Category is required", exception.getMessage());
    }

    @Test
    void shouldRejectTransactionWithoutDescription() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        null, 7L, AMOUNT, TransactionType.PURCHASE, OCCURRED_AT,
                        null, "Groceries", TransactionStatus.APPROVED,
                        null, null
                )
        );

        assertEquals("Description is required", exception.getMessage());
    }

    @Test
    void shouldRejectRejectedTransactionWithoutReason() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        null, 7L, AMOUNT, TransactionType.PURCHASE, OCCURRED_AT,
                        "Grocery purchase", "Groceries", TransactionStatus.REJECTED,
                        null, null
                )
        );

        assertEquals(
                "Rejection reason is required for rejected transactions",
                exception.getMessage()
        );
    }

    @Test
    void shouldCreateTransferWithDestinationUser() {
        assertDoesNotThrow(() -> new Transaction(
                null, 7L, AMOUNT, TransactionType.TRANSFER, OCCURRED_AT,
                "Transfer to another user", "Transfer", TransactionStatus.APPROVED,
                8L, null
        ));
    }
}