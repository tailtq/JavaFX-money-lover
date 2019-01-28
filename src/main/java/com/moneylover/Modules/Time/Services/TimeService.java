package com.moneylover.Modules.Time.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Time.Entities.Time;
import com.moneylover.Modules.Wallet.Entities.UserWallet;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class TimeService extends BaseService {
    public TimeService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return Time.getTable();
    }

    public ArrayList<Time> list() throws SQLException {
        ArrayList<Time> times = this._list();

        return times;
    }

    public Time getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);

        if (resultSet.wasNull()) {
            throw new NotFoundException();
        }

        Time time = this._toObject(resultSet);
        this.closeStatement();

        return time;
    }

    public Time getDetail(int month, int year) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getDetailBy("month = " + month, "year = " + year);

        if (resultSet.wasNull()) {
            throw new NotFoundException();
        }
        if (!resultSet.next()) {
            return null;
        }

        Time time = this._toObject(resultSet);
        this.closeStatement();

        return time;
    }

    public Time create(Time time) throws SQLException, NotFoundException {
        int id = this._create(time);

        return this.getDetail(id);
    }

    public boolean create(ArrayList<Time> times) throws SQLException, NotFoundException {
        this._create(times);

        return true;
    }

    public Time update(Time time, int id) throws SQLException, NotFoundException {
        this._update(time, id);

        return this.getDetail(id);
    }

    /*====================================================================================*/
    private ArrayList<Time> _list() throws SQLException {
        ArrayList<Time> times = new ArrayList<>();
        ResultSet resultSet = this.get();

        while (resultSet.next()) {
            times.add(this._toObject(resultSet));
        }

        return times;
    }

    private ArrayList<Time> _list(String[] ids) throws SQLException {
        ArrayList<Time> times = new ArrayList<>();
        ResultSet resultSet = this.get("id in (" + String.join(",", ids) + ")" );

        while (resultSet.next()) {
            times.add(this._toObject(resultSet));
        }

        return times;
    }

    private int _create(Time time) throws SQLException {
        String statementString = "INSERT INTO times(month, year, created_at) VALUES (?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, time.getMonth());
        statement.setInt(2, time.getYear());
        statement.setTimestamp(3, this.getCurrentTime());
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private void _create(ArrayList<Time> times) throws SQLException {
        String statementString = "INSERT INTO times(month, year, created_at) VALUES (?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        int i = 0;

        for (Time time: times) {
            statement.setInt(1, time.getMonth());
            statement.setInt(2, time.getYear());
            statement.setTimestamp(3, this.getCurrentTime());
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == times.size()) {
                statement.executeBatch(); // Execute every 1000 items.
            }
        }
    }

    private int _update(Time time, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        state.setInt(2, id)
//        statement.setDouble(1, time.getAmount());

        return statement.executeUpdate();
    }

    @Override
    protected Time _toObject(ResultSet resultSet) throws SQLException {
        Time time = new Time();
        time.setId(resultSet.getInt("id"));
        time.setMonth(resultSet.getInt("month"));
        time.setYear(resultSet.getInt("year"));
        time.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        time.setUpdatedAt(this.getUpdatedAt(resultSet.getTimestamp("updated_at")));

        return time;
    }
}
