package com.moneylover.Modules.Transaction.Controllers;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Budget.Entities.Budget;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Transaction.Services.TransactionService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionController {
    private TransactionService service;

    public TransactionController() throws SQLException, ClassNotFoundException {
        service = new TransactionService();
    }

    public ArrayList<Transaction> listByMonth(int walletId, LocalDate date, char operator) throws SQLException {
        ArrayList<Transaction> transactions = this.service.listByMonth(walletId, date, operator);

        return transactions;
    }

    public ArrayList<Transaction> listByDateRange(int walletId, LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Transaction> transactions = this.service.listByDateRange(walletId, startDate, endDate);

        return transactions;
    }

    public ArrayList<Transaction> listNotReportedByDateRange(int walletId, LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Transaction> transactions = this.service.listNotReportedByDateRange(walletId, startDate, endDate);

        return transactions;
    }

    public ArrayList<Transaction> listByBudget(Budget budget) throws SQLException {
        ArrayList<Transaction> transactions = this.service.listByBudget(budget);

        return transactions;
    }

    public Transaction getDetail(int id) throws SQLException, NotFoundException {
        Transaction transaction = this.service.getDetail(id);

        return transaction;
    }

    public Transaction create(Transaction transaction) throws SQLException, NotFoundException {
        Transaction newTransaction = this.service.create(transaction);

        return newTransaction;
    }

    public boolean create(ArrayList<Transaction> transactions) throws SQLException, NotFoundException {
        this.service.create(transactions);

        return true;
    }

    public boolean update(Transaction transaction, int id) throws SQLException, NotFoundException, ClassNotFoundException {
        this.service.update(transaction, id);

        return true;
    }

    public boolean delete(int id) throws SQLException, NotFoundException {
        this.service.delete(id);

        return true;
    }
}
