package com.moneylover.Modules.Friend.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Friend.Entities.Friend;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class FriendService extends BaseService {
    public FriendService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return Friend.getTable();
    }

    public ArrayList<Friend> list(int userId) throws SQLException {
        ArrayList<Friend> friends = this._list(userId);

        return friends;
    }

    public Friend getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Friend friend = this._toObject(resultSet);
        this.closeStatement();

        return friend;
    }

    public Friend create(Friend friend) throws SQLException, NotFoundException {
        int id = this._create(friend);

        return this.getDetail(id);
    }

    public boolean create(ArrayList<Friend> friends) throws SQLException {
        this._create(friends);

        return true;
    }

    public void update(Friend friend, int id) throws SQLException {
        this._update(friend, id);
    }

    /*====================================================================================*/
    private ArrayList<Friend> _list(int userId) throws SQLException {
        ArrayList<Friend> friends = new ArrayList<>();
        ResultSet resultSet = this.get("user_id = " + userId);

        while (resultSet.next()) {
            friends.add(this._toObject(resultSet));
        }

        return friends;
    }

    private int _create(Friend friend) throws SQLException {
        String statementString = "INSERT INTO friends(user_id, name, image, created_at) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, friend.getUserId());
        statement.setString(2, friend.getName());
        statement.setString(3, friend.getImage());
        statement.setTimestamp(4, this.getCurrentTime());
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private boolean _create(ArrayList<Friend> friends) throws SQLException {
        String statementString = "INSERT INTO friends(user_id, name, image, created_at) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        int i = 0;

        for (Friend friend : friends) {
            statement.setInt(1, friend.getUserId());
            statement.setString(2, friend.getName());
            statement.setString(3, friend.getImage());
            statement.setTimestamp(4, this.getCurrentTime());
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == friends.size()) {
                statement.executeBatch(); // Execute every 1000 items.
                statement.clearBatch();
            }
        }

        return true;
    }

    private void _update(Friend friend, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET user_id = ?, name = ?, image = ?, updated_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        statement.setInt(1, friend.getUserId());
        statement.setString(2, friend.getName());
        statement.setString(3, friend.getImage());
        statement.setTimestamp(4, this.getCurrentTime());
        statement.setInt(5, id);
        statement.executeUpdate();
        this.closePreparedStatement();
    }

    @Override
    protected Friend _toObject(ResultSet resultSet) throws SQLException {
        Friend friend = new Friend();
        friend.setId(resultSet.getInt("id"));
        friend.setUserId(resultSet.getInt("user_id"));
        friend.setName(resultSet.getString("name"));
        friend.setImage(resultSet.getString("image"));
        friend.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        friend.setUpdatedAt(this.getUpdatedAt(resultSet.getTimestamp("updated_at")));

        return friend;
    }
}
