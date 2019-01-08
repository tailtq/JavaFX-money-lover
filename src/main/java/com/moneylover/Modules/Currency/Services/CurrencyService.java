package com.moneylover.Modules.Currency.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Currency.Entities.Currency;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public Currency create(Currency currency) throws SQLException, NotFoundException {
        int id = this._create(currency);

        return this.getDetail(id);
    }

    public Currency update(Currency currency, int id) throws SQLException, NotFoundException {
        this._update(currency, id);

        return this.getDetail(id);
    }

    private ArrayList<Currency> _list() throws SQLException {
        ArrayList<Currency> currencies = new ArrayList<Currency>();
        ResultSet resultSet = this.get();

        while (resultSet.next()) {
            // Continue
        }

        return currencies;
    }

    public Currency getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this.getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Currency currency = new Currency();
        // Continue

        return currency;
    }

    private int _create(Currency currency) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "() VALUES (?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        statement.setDouble(1, currency.getAmount());

        return statement.executeUpdate();
    }

    private int _update(Currency currency, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        state.setInt(2, id)
//        statement.setDouble(1, currency.getAmount());

        return statement.executeUpdate();
    }
}
