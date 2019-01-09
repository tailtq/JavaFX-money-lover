package com.moneylover.Modules.User.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.User.Entities.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class UserService extends BaseService {
    public UserService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return User.getTable();
    }

    public ArrayList<User> list() throws SQLException {
        ArrayList<User> users = this._list();

        return users;
    }

    public User create(User user) throws SQLException, NotFoundException {
        int id = this._create(user);

        return this.getDetail(id);
    }

    public User update(User user, int id) throws SQLException, NotFoundException {
        this._update(user, id);

        return this.getDetail(id);
    }

    private ArrayList<User> _list() throws SQLException {
        ArrayList<User> users = new ArrayList<User>();
        ResultSet resultSet = this.get();

        while (resultSet.next()) {
            // Continue
        }

        return users;
    }

    public User getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        User user = new User();
        // Continue

        return user;
    }

    private int _create(User user) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(name, email, password, created_at) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);

        LocalDate currentDate = LocalDate.now();
        statement.setString(1, user.getName());
        statement.setString(1, user.getPassword());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        statement.setDate(4, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));

        return statement.executeUpdate();
    }

    private int _update(User user, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        state.setInt(2, id)
//        statement.setDouble(1, user.getAmount());

        return statement.executeUpdate();
    }
}
