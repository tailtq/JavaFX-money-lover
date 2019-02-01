package com.moneylover.Modules.Type.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Type.Entities.Type;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class TypeService extends BaseService {
    public TypeService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return Type.getTable();
    }

    public ArrayList<Type> list() throws SQLException {
        ArrayList<Type> types = this._list();

        return types;
    }

    public ArrayList<Type> list(String ...names) throws SQLException {
        ArrayList<Type> types = this._list(names);

        return types;
    }

    public Type getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Type type = this._toObject(resultSet);
        this.closeStatement();

        return type;
    }

    public Type create(Type type) throws SQLException, NotFoundException {
        int id = this._create(type);

        return this.getDetail(id);
    }

    public boolean create(ArrayList<Type> types) throws SQLException {
        this._create(types);

        return true;
    }

    public Type update(Type type, int id) throws SQLException, NotFoundException {
        this._update(type, id);

        return this.getDetail(id);
    }

    /*====================================================================================*/
    private ArrayList<Type> _list() throws SQLException {
        ArrayList<Type> types = new ArrayList<>();
        ResultSet resultSet = this.get();

        while (resultSet.next()) {
            types.add(this._toObject(resultSet));
        }

        return types;
    }

    private ArrayList<Type> _list(String ...names) throws SQLException {
        ArrayList<Type> types = new ArrayList<>();
        ResultSet resultSet = this.get("money_type in (" + String.join(",", names) + ")");

        while (resultSet.next()) {
            types.add(this._toObject(resultSet));
        }

        return types;
    }

    private int _create(Type type) throws SQLException {
        String statementString = "INSERT INTO types(money_type, name, created_at) VALUES (?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, type.getMoneyType());
        statement.setString(2, type.getName());
        statement.setTimestamp(3, this.getCurrentTime());
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private boolean _create(ArrayList<Type> types) throws SQLException {
        String statementString = "INSERT INTO types(money_type, name, created_at) VALUES (?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        int i = 0;

        for (Type type : types) {
            statement.setString(1, type.getMoneyType());
            statement.setString(2, type.getName());
            statement.setTimestamp(3, this.getCurrentTime());
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == types.size()) {
                statement.executeBatch(); // Execute every 1000 items.
                statement.clearBatch();
            }
        }

        return true;
    }

    private void _update(Type type, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        statement.executeUpdate();
    }

    @Override
    protected Type _toObject(ResultSet resultSet) throws SQLException {
        Type type = new Type();
        type.setId(resultSet.getInt("id"));
        type.setMoneyType(resultSet.getString("money_type"));
        type.setName(resultSet.getString("name"));
        type.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        type.setUpdatedAt(this.getUpdatedAt(resultSet.getTimestamp("updated_at")));

        return type;
    }
}
