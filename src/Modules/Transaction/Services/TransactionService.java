package Modules.Transaction.Services;

import Infrastructure.Exceptions.NotFoundException;
import Infrastructure.Services.BaseService;
import Modules.Transaction.Entities.Transaction;

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

    public ArrayList<Transaction> list(int timeId) throws SQLException {
        ArrayList<Transaction> transactions = this._list(timeId);

        return transactions;
    }

    public Transaction create(Transaction transaction) throws SQLException, NotFoundException {
        int id = this._create(transaction);

        return this.getDetail(id);
    }

    public Transaction update(Transaction transaction, int id) throws SQLException, NotFoundException {
        this._update(transaction, id);

        return this.getDetail(id);
    }

    private ArrayList<Transaction> _list(int timeId) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        ResultSet resultSet = this.get("time_id = " + timeId);

        while (resultSet.next()) {
            // Continue
        }

        return transactions;
    }

    public Transaction getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this.getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Transaction transaction = new Transaction();
        // Continue

        return transaction;
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
}
