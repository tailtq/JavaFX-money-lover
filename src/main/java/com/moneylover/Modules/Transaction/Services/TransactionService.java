package com.moneylover.Modules.Transaction.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Transaction.Entities.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionService extends BaseService {
    public TransactionService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return Transaction.getTable();
    }

    public ArrayList<Transaction> list(int month) throws SQLException {
        ArrayList<Transaction> transactions = this._list(month);

        return transactions;
    }

    public Transaction getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Transaction transaction = new Transaction();
        // Continue

        return transaction;
    }

    public Transaction create(Transaction transaction) throws SQLException, NotFoundException {
        int id = this._create(transaction);

        return this.getDetail(id);
    }

    public Transaction update(Transaction transaction, int id) throws SQLException, NotFoundException {
        this._update(transaction, id);

        return this.getDetail(id);
    }

    /*====================================================================================*/
    private ArrayList<Transaction> _list(int month) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "INNER JOIN times ON transactions.time_id = times.id",
                "month = " + month
        );

        while (resultSet.next()) {
            transactions.add(this.toObject(resultSet));
        }

        return transactions;
    }

    private int _create(Transaction transaction) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "() VALUES (?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        statement.setDouble(1, transaction.getAmount());

        return statement.executeUpdate();
    }

    private int _update(Transaction transaction, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        state.setInt(2, id)
//        statement.setDouble(1, transaction.getAmount());

        return statement.executeUpdate();
    }

    @Override
    protected Transaction toObject(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();

        return transaction;
    }
}
