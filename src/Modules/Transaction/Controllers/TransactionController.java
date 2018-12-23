package Modules.Transaction.Controllers;

import Infrastructure.Exceptions.NotFoundException;
import Modules.Transaction.Entities.Transaction;
import Modules.Transaction.Services.TransactionService;

import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionController {
    private TransactionService service;

    public TransactionController() throws SQLException, ClassNotFoundException {
        service = new TransactionService();
    }

    public ArrayList<Transaction> list(int timeId) throws SQLException {
        ArrayList<Transaction> transactions = this.service.list(timeId);

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

    public Transaction update(Transaction transaction, int id) throws SQLException, NotFoundException {
        Transaction updatedTransaction = this.service.update(transaction, id);

        return updatedTransaction;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }
}
