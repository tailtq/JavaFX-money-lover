package com.moneylover.Modules.Currency.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Currency.Entities.Currency;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            currencies.add(this.toObject(resultSet));
        }

        return currencies;
    }

    private int _create(Currency currency) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(name, symbol, image, code, created_at) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);

        LocalDate currentDate = LocalDate.now();
        statement.setString(1, currency.getName());
        statement.setString(2, currency.getSymbol());
        statement.setString(3, currency.getImage());
        statement.setString(4, currency.getCode());
        statement.setDate(5, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));

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

    @Override
    protected Currency toObject(ResultSet resultSet) throws SQLException {
        Currency currency = new Currency();
        currency.setId(resultSet.getInt("id"));
        currency.setName(resultSet.getNString("name"));
        currency.setImage(resultSet.getString("image"));
        currency.setSymbol(resultSet.getNString("symbol"));
        currency.setCode(resultSet.getNString("code"));
        currency.setCreatedAt(resultSet.getDate("created_at"));
        currency.setUpdatedAt(resultSet.getDate("updated_at"));

        return currency;
    }
}
