package com.moneylover.Modules.Country.Controllers;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Country.Entities.Country;
import com.moneylover.Modules.Country.Services.CountryService;

import java.sql.SQLException;
import java.util.ArrayList;

public class CountryController {
    private CountryService service;

    public CountryController() throws SQLException, ClassNotFoundException {
        service = new CountryService();
    }

    public ArrayList<Country> list() throws SQLException {
        ArrayList<Country> countries = this.service.list();

        return countries;
    }

    public Country getDetail(int id) throws SQLException, NotFoundException {
        Country country = this.service.getDetail(id);

        return country;
    }

    public Country create(Country country) throws SQLException, NotFoundException {
        Country newCountry = this.service.create(country);

        return newCountry;
    }

    public Country update(Country country, int id) throws SQLException, NotFoundException {
        Country updatedCountry = this.service.update(country, id);

        return updatedCountry;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }
}
