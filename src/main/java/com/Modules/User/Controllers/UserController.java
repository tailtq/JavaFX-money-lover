package main.java.com.Modules.User.Controllers;

import main.java.com.Infrastructure.Exceptions.NotFoundException;
import main.java.com.Modules.User.Entities.User;
import main.java.com.Modules.User.Services.UserService;

import java.sql.SQLException;
import java.util.ArrayList;

public class UserController {
    private UserService service;

    public UserController() throws SQLException, ClassNotFoundException {
        service = new UserService();
    }

    public ArrayList<User> list() throws SQLException {
        ArrayList<User> users = this.service.list();

        return users;
    }

    public User getDetail(int id) throws SQLException, NotFoundException {
        User user = this.service.getDetail(id);

        return user;
    }

    public User create(User user) throws SQLException, NotFoundException {
        User newUser = this.service.create(user);

        return newUser;
    }

    public User update(User user, int id) throws SQLException, NotFoundException {
        User updatedUser = this.service.update(user, id);

        return updatedUser;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }
}
