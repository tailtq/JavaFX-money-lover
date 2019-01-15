package com.moneylover.Modules.Transaction.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Transaction.Entities.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
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

    public boolean create(ArrayList<Transaction> transactions) throws SQLException, NotFoundException {
        this._create(transactions);

        return true;
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
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        LocalDate currentDate = LocalDate.now();
        statement.setInt(1, transaction.getWalletId());
        statement.setInt(2, transaction.getTimeId());
        statement.setInt(3, transaction.getTypeId());
        statement.setInt(4, transaction.getCategoryId());
        statement.setInt(5, transaction.getSubCategoryId());
        statement.setDate(6, Date.valueOf(transaction.getTransactedAt().toString()));
        statement.setFloat(7, transaction.getAmount());
        statement.setString(8, transaction.getLocation());
        statement.setNString(9, transaction.getNote());
        statement.setString(10, transaction.getImage());
        statement.setByte(11, transaction.getIsReported());
        statement.setDate(12, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private int _create(ArrayList<Transaction> transactions) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(wallet_id, time_id, type_id, category_id, sub_category_id, transacted_at, amount, location, note, image, is_reported, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        int i = 0;

        for (Transaction transaction: transactions) {
            LocalDate currentDate = LocalDate.now();
            statement.setInt(1, transaction.getWalletId());
            statement.setInt(2, transaction.getTimeId());
            statement.setInt(3, transaction.getTypeId());
            statement.setInt(4, transaction.getCategoryId());
            statement.setInt(5, transaction.getSubCategoryId());
            statement.setDate(6, Date.valueOf(transaction.getTransactedAt().toString()));
            statement.setFloat(7, transaction.getAmount());
            statement.setString(8, transaction.getLocation());
            statement.setNString(9, transaction.getNote());
            statement.setString(10, transaction.getImage());
            statement.setByte(11, transaction.getIsReported());
            statement.setDate(12, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == transactions.size()) {
                statement.executeBatch(); // Execute every 1000 items.
            }
        }

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
