package com.moneylover.Modules.Currency.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Currency.Entities.Currency;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class CurrencyService extends BaseService {
    public CurrencyService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return Currency.getTable();
    }

    public ArrayList<Currency> list() throws SQLException {
        ArrayList<Currency> currencies = this._list();

        return currencies;
    }

    public Currency getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);

        if (resultSet.wasNull()) {
            throw new NotFoundException();
        }

        Currency currency = new Currency();
//         Continue
//        closeConnection();

        return currency;
    }

    public Currency create(Currency currency) throws SQLException, NotFoundException {
        int id = this._create(currency);

        return this.getDetail(id);
    }

    public Currency update(Currency currency, int id) throws SQLException, NotFoundException {
        this._update(currency, id);

        return this.getDetail(id);
    }

    /*====================================================================================*/

    private ArrayList<Currency> _list() throws SQLException {
        ArrayList<Currency> currencies = new ArrayList<>();
        ResultSet resultSet = this.get();

        while (resultSet.next()) {
            currencies.add(this._toObject(resultSet));
        }

        return currencies;
    }

    private int _create(Currency currency) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(name, symbol, icon, code, created_at) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, currency.getName());
        statement.setString(2, currency.getSymbol());
        statement.setString(3, currency.getIcon());
        statement.setString(4, currency.getCode());
        statement.setTimestamp(5, this.getCurrentTime());
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private int _update(Currency currency, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        state.setInt(2, id)
//        statement.setDouble(1, currency.getAmount());

        return statement.executeUpdate();
    }

    @Override
    protected Currency _toObject(ResultSet resultSet) throws SQLException {
        Currency currency = new Currency();
        currency.setId(resultSet.getInt("id"));
        currency.setName(resultSet.getNString("name"));
        currency.setIcon(resultSet.getString("icon"));
        currency.setSymbol(resultSet.getNString("symbol"));
        currency.setCode(resultSet.getNString("code"));
        currency.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        currency.setUpdatedAt(this.getUpdatedAt(resultSet.getTimestamp("updated_at")));

        return currency;
    }
}
