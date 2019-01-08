package com.moneylover.Modules.Currency.Controllers;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Currency.Entities.Currency;
import com.moneylover.Modules.Currency.Services.CurrencyService;

import java.sql.SQLException;
import java.util.ArrayList;

public class CurrencyController {
    private CurrencyService service;

    public CurrencyController() throws SQLException, ClassNotFoundException {
        service = new CurrencyService();
    }

    public ArrayList<Currency> list() throws SQLException {
        ArrayList<Currency> currencies = this.service.list();

        return currencies;
    }

    public Currency getDetail(int id) throws SQLException, NotFoundException {
        Currency currency = this.service.getDetail(id);

        return currency;
    }

    public Currency create(Currency currency) throws SQLException, NotFoundException {
        Currency newCurrency = this.service.create(currency);

        return newCurrency;
    }

    public Currency update(Currency currency, int id) throws SQLException, NotFoundException {
        Currency updatedCurrency = this.service.update(currency, id);

        return updatedCurrency;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }
}
