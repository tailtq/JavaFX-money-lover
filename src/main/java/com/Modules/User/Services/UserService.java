package main.java.com.Modules.User.Services;

import main.java.com.Infrastructure.Exceptions.NotFoundException;
import main.java.com.Infrastructure.Services.BaseService;
import main.java.com.Modules.User.Entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        ResultSet resultSet = this.getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        User user = new User();
        // Continue

        return user;
    }

    private int _create(User user) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "() VALUES (?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        statement.setDouble(1, user.getAmount());

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
