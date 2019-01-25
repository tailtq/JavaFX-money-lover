package com.moneylover.Modules.Category.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Category.Entities.Category;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class CategoryService extends BaseService {
    public CategoryService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return Category.getTable();
    }

    public ArrayList<Category> list() throws SQLException {
        ArrayList<Category> categories = this._list();

        return categories;
    }

    public ArrayList<Category> list(int typeId) throws SQLException {
        ArrayList<Category> categories = this._list(typeId);

        return categories;
    }

    public Category getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Category category = this.toObject(resultSet);
        this.closeStatement();

        return category;
    }

    public Category getDetail(String name) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getDetailBy("name = '" + name + "'");

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Category category = this.toObject(resultSet);
        this.closeStatement();

        return category;
    }

    public Category create(Category category) throws SQLException, NotFoundException {
        int id = this._create(category);

        return this.getDetail(id);
    }

    public boolean create(ArrayList<Category> categories) throws SQLException {
        this._create(categories);

        return true;
    }

    public Category update(Category category, int id) throws SQLException, NotFoundException {
        this._update(category, id);

        return this.getDetail(id);
    }

    /*====================================================================================*/
    private ArrayList<Category> _list() throws SQLException {
        ArrayList<Category> categories = new ArrayList<>();
        ResultSet resultSet = this.get();

        while (resultSet.next()) {
            categories.add(this.toObject(resultSet));
        }

        return categories;
    }

    private ArrayList<Category> _list(int typeId) throws SQLException {
        ArrayList<Category> categories = new ArrayList<>();
        ResultSet resultSet = this.get("type_id = " + typeId);

        while (resultSet.next()) {
            categories.add(this.toObject(resultSet));
        }

        return categories;
    }

    private int _create(Category category) throws SQLException {
        String statementString = "INSERT INTO categories(type_id, money_type, name, icon, created_at) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        LocalDate currentDate = LocalDate.now();
        statement.setInt(1, category.getTypeId());
        statement.setString(2, category.getMoneyType());
        statement.setString(3, category.getName());
        statement.setString(4, category.getIcon());
        statement.setDate(5, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private boolean _create(ArrayList<Category> categories) throws SQLException {
        String statementString = "INSERT INTO categories(type_id, money_type, name, icon, created_at) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        int i = 0;

        for (Category category : categories) {
            LocalDate currentDate = LocalDate.now();
            statement.setInt(1, category.getTypeId());
            statement.setString(2, category.getMoneyType());
            statement.setString(3, category.getName());
            statement.setString(4, category.getIcon());
            statement.setDate(5, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == categories.size()) {
                statement.executeBatch(); // Execute every 1000 items.
            }
        }

        return true;
    }

    private int _update(Category category, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        state.setInt(2, id)
//        statement.setDouble(1, category.getAmount());

        return statement.executeUpdate();
    }

    @Override
    protected Category toObject(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setId(resultSet.getInt("id"));
        category.setTypeId(resultSet.getInt("type_id"));
        category.setMoneyType(resultSet.getString("money_type"));
        category.setName(resultSet.getString("name"));
        category.setIcon(resultSet.getString("icon"));
        category.setCreatedAt(resultSet.getDate("created_at"));
        category.setUpdatedAt(resultSet.getDate("updated_at"));

        return category;
    }
}
