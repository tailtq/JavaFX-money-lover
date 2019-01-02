package com.moneylover.Modules.Wallet.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Wallet.Entities.Wallet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WalletService extends BaseService {
    public WalletService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return Wallet.getTable();
    }

    public ArrayList<Wallet> list() throws SQLException {
        ArrayList<Wallet> wallets = this._list();

        return wallets;
    }

    public Wallet create(Wallet wallet) throws SQLException, NotFoundException {
        int id = this._create(wallet);

        return this.getDetail(id);
    }

    public Wallet update(Wallet wallet, int id) throws SQLException, NotFoundException {
        this._update(wallet, id);

        return this.getDetail(id);
    }

    private ArrayList<Wallet> _list() throws SQLException {
        ArrayList<Wallet> wallets = new ArrayList<Wallet>();
        ResultSet resultSet = this.get();

        while (resultSet.next()) {
            // Continue
        }

        return wallets;
    }

    public Wallet getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this.getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Wallet wallet = new Wallet();
        // Continue

        return wallet;
    }

    private int _create(Wallet wallet) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "() VALUES (?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        statement.setDouble(1, wallet.getAmount());

        return statement.executeUpdate();
    }

    private int _update(Wallet wallet, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        state.setInt(2, id)
//        statement.setDouble(1, wallet.getAmount());

        return statement.executeUpdate();
    }
}
