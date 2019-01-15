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
        transaction.setId(resultSet.getInt("id"));
        transaction.setWalletId(resultSet.getInt("wallet_id"));
        transaction.setTimeId(resultSet.getInt("time_id"));
        transaction.setTypeId(resultSet.getInt("type_id"));
        transaction.setCategoryId(resultSet.getInt("category_id"));
        transaction.setSubCategoryId(resultSet.getInt("sub_category_id"));
        transaction.setTransactedAt(resultSet.getDate("transacted_at"));
        transaction.setAmount(resultSet.getFloat("amount"));
        transaction.setLocation(resultSet.getString("location"));
        transaction.setNote(resultSet.getNString("note"));
        transaction.setImage(resultSet.getString("image"));
        transaction.setIsReported(resultSet.getByte("is_reported"));
        transaction.setCreatedAt(resultSet.getDate("created_at"));
        transaction.setUpdatedAt(resultSet.getDate("updated_at"));

        return transaction;
    }
}
