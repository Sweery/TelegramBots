package org.example.service;

import org.example.model.Transaction;
import org.example.model.TransactionType;

import java.util.Collections;
import java.util.List;

public class FinanceService {
    public void addTransaction(long userId, TransactionType type, double amount, String name) {
    }

    public List<Transaction> getTransactions(long userId, TransactionType type) {
        return Collections.emptyList();
    }

    public boolean deleteTransaction(long userId, int transactionId) {
        return false;
    }

    public boolean updateTransaction(long userId, int transactionId, double newAmount, String newName) {
        return false;
    }

    public double getSum(long userId, TransactionType type) {
        return 0.0;
    }
}
