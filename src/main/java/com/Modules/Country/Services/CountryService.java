package main.java.com.Modules.Country.Services;

import main.java.com.Infrastructure.Exceptions.NotFoundException;
import main.java.com.Infrastructure.Services.BaseService;
import main.java.com.Modules.Country.Entities.Country;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CountryService extends BaseService {
    public CountryService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return Country.getTable();
    }

    public ArrayList<Country> list() throws SQLException {
        ArrayList<Country> countries = this._list();

        return countries;
    }

    public Country create(Country country) throws SQLException, NotFoundException {
        int id = this._create(country);

        return this.getDetail(id);
    }

    public Country update(Country country, int id) throws SQLException, NotFoundException {
        this._update(country, id);

        return this.getDetail(id);
    }

    private ArrayList<Country> _list() throws SQLException {
        ArrayList<Country> countries = new ArrayList<Country>();
        ResultSet resultSet = this.get();

        while (resultSet.next()) {
            // Continue
        }

        return countries;
    }

    public Country getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this.getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Country country = new Country();
        // Continue

        return country;
    }

    private int _create(Country country) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "() VALUES (?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        statement.setDouble(1, country.getAmount());

        return statement.executeUpdate();
    }

    private int _update(Country country, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        state.setInt(2, id)
//        statement.setDouble(1, country.getAmount());

        return statement.executeUpdate();
    }
}
