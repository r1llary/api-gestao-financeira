package com.beca.transacoes.core.conta.service;

import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class AccountValidatorTest {

    @Test
    void shouldRejectInsufficientBalance(){
        Account account = new Account(
        6L,
        new BigDecimal("100"),
        new BigDecimal("500"),
        new BigDecimal("200")
        );

        AccountValidator validator = new AccountValidator();

        String result = validator.validateBalance(
                account,
                new BigDecimal("150")

        );

        assertEquals("Insufficient balance", result);
    }

    @Test
    void shouldRejectTransactionLimitExceeded(){
        Account account = new Account(
                6L,
                new BigDecimal("1000"),
                new BigDecimal("500"),
                new BigDecimal("200")
        );

        AccountValidator validator = new AccountValidator();

        String result = validator.validateTransactionLimit(
                account,
                new BigDecimal("250")
        );

        assertEquals("Transaction limit exceeded", result);
    }

    @Test
    void shouldRejectDailyLimitExceeded(){
        Account account = new Account(
                6L,
                new BigDecimal("1000"),
                new BigDecimal("500"),
                new BigDecimal("200")
        );

        AccountValidator validator = new AccountValidator();

        String result = validator.validateDailyLimit(
                account,
                new BigDecimal("400"),
                new BigDecimal("150")
        );

        assertEquals("Daily limit exceeded", result);
    }

    @Test
    void shouldAllowValuesEqualToLimits() {

        Account account = new Account(
                6L,
                new BigDecimal("200"),
                new BigDecimal("500"),
                new BigDecimal("200")
        );

        AccountValidator validator = new AccountValidator();

        BigDecimal amount = new BigDecimal("200");

        assertNull(validator.validateBalance(account, amount));

        assertNull(validator.validateTransactionLimit(account, amount));

        assertNull(validator.validateDailyLimit(
                account,
                new BigDecimal("300"),
                amount
        ));
    }

    @Test
    void shouldAllowTransactionWithinLimits() {

        Account account = new Account(
                6L,
                new BigDecimal("1000"),
                new BigDecimal("500"),
                new BigDecimal("200")
        );

        AccountValidator validator = new AccountValidator();

        BigDecimal amount = new BigDecimal("100");

        assertNull(validator.validateBalance(account, amount));

        assertNull(validator.validateTransactionLimit(account, amount));

        assertNull(validator.validateDailyLimit(
                account,
                new BigDecimal("200"),
                amount
        ));
    }

    @Test
    void shouldIgnoreDepositsInDailySpending(){
        Transaction deposit=mock(Transaction.class);

        when(deposit.getType()).thenReturn(TransactionType.DEPOSIT);

        AccountValidator validator = new AccountValidator();

        BigDecimal total = validator.calculateDailySpending(
                List.of(deposit)
        );

        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void shouldCalculateDailySpending() {

        Transaction transfer = mock(Transaction.class);
        Transaction purchase = mock(Transaction.class);

        when(transfer.getType()).thenReturn(TransactionType.TRANSFER);
        when(transfer.getAmount()).thenReturn(new BigDecimal("100"));

        when(purchase.getType()).thenReturn(TransactionType.PURCHASE);
        when(purchase.getAmount()).thenReturn(new BigDecimal("150"));

        AccountValidator validator = new AccountValidator();

        BigDecimal total = validator.calculateDailySpending(
                List.of(transfer, purchase)
        );

        assertEquals(new BigDecimal("250"), total);
    }

    @Test
    void shouldRejectTransactionWhenDailyLimitExceeded() {

        Account account = new Account(
                6L,
                new BigDecimal("1000"),
                new BigDecimal("200"),
                new BigDecimal("200")
        );

        Transaction purchase = mock(Transaction.class);

        when(purchase.getType()).thenReturn(TransactionType.PURCHASE);
        when(purchase.getAmount()).thenReturn(new BigDecimal("150"));

        AccountValidator validator = new AccountValidator();

        String result = validator.validateTransaction(
                account,
                List.of(purchase),
                new BigDecimal("100")
        );

        assertEquals("Daily limit exceeded", result);
    }

}
