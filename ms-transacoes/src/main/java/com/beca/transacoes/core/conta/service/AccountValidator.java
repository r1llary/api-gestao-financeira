package com.beca.transacoes.core.conta.service;

import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public class AccountValidator {

    public String validateBalance(Account account, BigDecimal amount){
        if (account.balance().compareTo(amount)<0){
            return "Insufficient balance";
        }

        return null;
    }

    public String validateTransactionLimit(Account account, BigDecimal amount){
        if (amount.compareTo(account.transactionLimit()) >0){
            return "Transaction limit exceeded";
        }

        return null;
    }

    public String validateDailyLimit(Account account, BigDecimal totalSpentToday, BigDecimal amount){
        BigDecimal total = totalSpentToday.add(amount);

        if (total.compareTo(account.dailyLimit()) >0){
            return "Daily limit exceeded";
        }
        return null;
    }

    public BigDecimal calculateDailySpending(List<Transaction> transactions) {

        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            if (transaction.getType() != TransactionType.DEPOSIT) {
                total = total.add(transaction.getAmount());
            }
        }

        return total;
    }

    public String validateTransaction(
            Account account,
            List<Transaction> transactions,
            BigDecimal amount
    ) {
        String error = validateBalance(account, amount);

        if (error != null) {
            return error;
        }

        error = validateTransactionLimit(account, amount);

        if (error != null) {
            return error;
        }

        BigDecimal totalSpentToday = calculateDailySpending(transactions);

        return validateDailyLimit(account, totalSpentToday, amount);
    }


}

