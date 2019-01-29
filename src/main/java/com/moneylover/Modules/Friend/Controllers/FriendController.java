package com.moneylover.Modules.Friend.Controllers;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Friend.Entities.Friend;
import com.moneylover.Modules.Friend.Services.FriendService;

import java.sql.SQLException;
import java.util.ArrayList;

public class FriendController {
    private FriendService service;

    public FriendController() throws SQLException, ClassNotFoundException {
        service = new FriendService();
    }

    public ArrayList<Friend> list(int userId) throws SQLException {
        ArrayList<Friend> friends = this.service.list(userId);

        return friends;
    }

    public Friend getDetail(int id) throws SQLException, NotFoundException {
        Friend friend = this.service.getDetail(id);

        return friend;
    }

    public Friend create(Friend friend) throws SQLException, NotFoundException {
        Friend newFriend = this.service.create(friend);

        return newFriend;
    }

    public boolean create(ArrayList<Friend> friends) throws SQLException, NotFoundException {
        this.service.create(friends);

        return true;
    }

    public boolean update(Friend friend, int id) throws SQLException {
        this.service.update(friend, id);

        return true;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }
}
