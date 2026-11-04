package com.beca.transacoes.core.transacao.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void shouldRejectTransactionWithZeroAmount(){

        assertThrows(IllegalArgumentException.class, () -> new Transaction(
                7L,
                BigDecimal.ZERO,
                TransactionType.PURCHASE,
                Instant.parse("2026-11-04T12:00:00Z")
        )
        );
    }


        @Test
        void shouldCreateWithdrawalWithPositiveAmount(){
            BigDecimal amount = new BigDecimal("50.00");

            Transaction transaction = new Transaction(
                    7L,
                    amount,
                    TransactionType.WITHDRAWAL,
                    Instant.parse("2026-11-04T12:00:00Z")
            );

            assertEquals(amount, transaction.getAmount());
            assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
        }
}
