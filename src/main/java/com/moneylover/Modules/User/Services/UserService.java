package com.moneylover.Modules.User.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Helpers.UpdatableBcrypt;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.User.Entities.User;

import java.sql.*;
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
        this.closeStatement();

        return users;
    }

    public User getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);
        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        User user = toObject(resultSet);
        this.closeStatement();

        return user;
    }

    public User login(User user) throws SQLException, NotFoundException {
        ResultSet resultSet = this.get("email = '" + user.getEmail() + "'");
        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        String password = resultSet.getString("password");
        if (!UpdatableBcrypt.verifyHash(user.getPassword(), password)) {
            throw new NotFoundException();
        }

        user = this.toObject(resultSet);
        this.closeStatement();

        return user;
    }

    public User getUserByEmail(String email) throws SQLException, NotFoundException {
        ResultSet resultSet = this.get("email = '" + email + "'");
        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        User user = this.toObject(resultSet);
        this.closeStatement();

        return user;
    }

    public User create(User user) throws SQLException, NotFoundException {
        int id = this._create(user);

        return this.getDetail(id);
    }

    public boolean update(User user, int id) throws SQLException {
        this._update(user, id);

        return true;
    }

    /*====================================================================================*/

    private ArrayList<User> _list() throws SQLException {
        ArrayList<User> users = new ArrayList<User>();
        ResultSet resultSet = this.get();

        while (resultSet.next()) {
            users.add(this.toObject(resultSet));
        }

        return users;
    }

    private int _create(User user) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(name, email, password, created_at) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        LocalDate currentDate = LocalDate.now();
        statement.setNString(1, user.getName());
        statement.setString(2, user.getEmail());
        statement.setString(3, UpdatableBcrypt.hash(user.getPassword()));
        statement.setDate(4, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private boolean _update(User user, int id) throws SQLException {
        String passwordStatement = "";
        int i = 3;
        if (!user.getPassword().equals("")) {
            passwordStatement = "password = ?,";
        }

        String statementString = "UPDATE " + getTable() + " SET name = ?, phone = ?, " + passwordStatement + " updated_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);

        if (!user.getPassword().equals("")) {
            statement.setString(i++, UpdatableBcrypt.hash(user.getPassword()));
        }

        LocalDate currentDate = LocalDate.now();
        statement.setNString(1, user.getName());
        statement.setString(2, user.getPhone());
        statement.setDate(i++, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));
        statement.setDouble(i, id);
        statement.executeUpdate();
        this.closePreparedStatement();

        return true;
    }

    @Override
    protected User toObject(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getNString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setPhone(resultSet.getString("phone"));
        user.setBirthday(resultSet.getDate("birthday"));
        user.setCreatedAt(resultSet.getDate("created_at"));
        user.setUpdatedAt(resultSet.getDate("updated_at"));

        return user;
    }
}
