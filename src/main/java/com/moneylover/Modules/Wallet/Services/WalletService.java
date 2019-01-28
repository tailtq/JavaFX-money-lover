package com.moneylover.Modules.Wallet.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Wallet.Entities.UserWallet;
import com.moneylover.Modules.Wallet.Entities.Wallet;

import java.sql.*;
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

    /**
     * @param userId
     * @return
     * @throws SQLException
     */
    public ArrayList<Wallet> list(int userId) throws SQLException {
        ArrayList<Wallet> wallets = this._list(userId);

        return wallets;
    }

    public Wallet getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this.getByJoin(
                "wallets.*, currencies.symbol as money_symbol",
                "INNER JOIN user_wallet ON wallets.id = user_wallet.wallet_id " +
                        "INNER JOIN currencies ON wallets.currency_id = currencies.id",
                "wallets.id = " + id
        );

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Wallet wallet = this._toObject(resultSet);
        this.closeStatement();

        return wallet;
    }

    public Wallet create(Wallet wallet) throws SQLException, NotFoundException {
        int id = this._create(wallet);
        ResultSet resultSet = this.getByJoin(
                "wallets.*, currencies.symbol as money_symbol",
                "INNER JOIN currencies ON wallets.currency_id = currencies.id",
                "wallets.id = " + id
        );

        if (resultSet.next()) {
            wallet = this._toObject(resultSet);
        }

        this.closeStatement();

        return wallet;
    }

    public boolean attachUsers(ArrayList<UserWallet> userWallets) throws SQLException {
        boolean result = this._attachUsers(userWallets);
        this.closeStatement();

        return result;
    }

    public void update(Wallet wallet, int id) throws SQLException {
        this._update(wallet, id);
    }

    public void setAmount(float inflow, float outflow, int id) throws SQLException {
        this._setAmount(inflow, outflow, id);
    }

    public boolean delete(int id) throws SQLException {
        this.deleteBy("user_wallet", "wallet_id = " + id);

        return this.deleteById(id);
    }

    /*====================================================================================*/

    private ArrayList<Wallet> _list() throws SQLException {
        ArrayList<Wallet> wallets = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "wallets.*, currencies.symbol as money_symbol",
                "INNER JOIN user_wallet ON wallets.id = user_wallet.wallet_id " +
                        "INNER JOIN currencies ON wallets.currency_id = currencies.id"
        );

        while (resultSet.next()) {
            wallets.add(this._toObject(resultSet));
        }

        this.closeStatement();

        return wallets;
    }

    private ArrayList<Wallet> _list(int userId) throws SQLException {
        ArrayList<Wallet> wallets = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "wallets.*, currencies.symbol as money_symbol",
                "INNER JOIN user_wallet ON wallets.id = user_wallet.wallet_id " +
                        "INNER JOIN currencies ON wallets.currency_id = currencies.id",
                "user_id = " + userId
        );

        while (resultSet.next()) {
            wallets.add(this._toObject(resultSet));
        }

        this.closeStatement();

        return wallets;
    }

    private int _create(Wallet wallet) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(currency_id, name, inflow, outflow, created_at) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, wallet.getCurrencyId());
        statement.setString(2, wallet.getName());
        statement.setFloat(3, wallet.getInflow());
        statement.setFloat(4, wallet.getOutflow());
        statement.setTimestamp(5, this.getCurrentTime());
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private boolean _attachUsers(ArrayList<UserWallet> userWallets) throws SQLException {
        String statementString = "INSERT INTO user_wallet(user_id, wallet_id) VALUES (?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        int i = 0;

        for (UserWallet userWallet: userWallets) {
            statement.setInt(1, userWallet.getUserId());
            statement.setInt(2, userWallet.getWalletId());
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == userWallets.size()) {
                statement.executeBatch(); // Execute every 1000 items.
            }
        }

        return true;
    }

    private void _update(Wallet wallet, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET currency_id = ?, name = ?, inflow = ?, outflow = ?, updated_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        statement.setInt(1, wallet.getCurrencyId());
        statement.setNString(2, wallet.getName());
        statement.setFloat(3, wallet.getInflow());
        statement.setFloat(4, wallet.getOutflow());
        statement.setTimestamp(5, this.getCurrentTime());
        statement.setInt(6, id);
        statement.executeUpdate();
        this.closePreparedStatement();
    }

    private void _setAmount(float inflow, float outflow, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET inflow = ?, outflow = ?, updated_at = ? WHERE id = ?";

        PreparedStatement statement = this.getPreparedStatement(statementString);
        statement.setFloat(1, inflow);
        statement.setFloat(2, outflow);
        statement.setTimestamp(3, this.getCurrentTime());
        statement.setInt(4, id);
        statement.executeUpdate();
        this.closePreparedStatement();
    }

    @Override
    protected Wallet _toObject(ResultSet resultSet) throws SQLException {
        Wallet wallet = new Wallet();
        wallet.setId(resultSet.getInt("id"));
        wallet.setCurrencyId(resultSet.getInt("currency_id"));
        wallet.setName(resultSet.getNString("name"));
        wallet.setInflow(resultSet.getFloat("inflow"));
        wallet.setOutflow(resultSet.getFloat("outflow"));
        wallet.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        wallet.setUpdatedAt(this.getUpdatedAt(resultSet.getTimestamp("updated_at")));
        wallet.setMoneySymbol(resultSet.getNString("money_symbol"));

        return wallet;
    }
}
