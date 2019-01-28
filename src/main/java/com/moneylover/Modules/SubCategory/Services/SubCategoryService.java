package com.moneylover.Modules.SubCategory.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.SubCategory.Entities.SubCategory;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class SubCategoryService extends BaseService {
    public SubCategoryService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return SubCategory.getTable();
    }

    public ArrayList<SubCategory> list() throws SQLException {
        ArrayList<SubCategory> subCategories = this._list();

        return subCategories;
    }

    public ArrayList<SubCategory> list(int typeId) throws SQLException {
        ArrayList<SubCategory> subCategories = this._list(typeId);

        return subCategories;
    }

    public SubCategory getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        SubCategory subCategory = this._toObject(resultSet);
        this.closeStatement();

        return subCategory;
    }

    public SubCategory create(SubCategory subCategory) throws SQLException, NotFoundException {
        int id = this._create(subCategory);

        return this.getDetail(id);
    }

    public boolean create(ArrayList<SubCategory> subCategories) throws SQLException {
        this._create(subCategories);

        return true;
    }

    public SubCategory update(SubCategory subCategory, int id) throws SQLException, NotFoundException {
        this._update(subCategory, id);

        return this.getDetail(id);
    }

    /*====================================================================================*/
    private ArrayList<SubCategory> _list() throws SQLException {
        ArrayList<SubCategory> subCategories = new ArrayList<>();
        ResultSet resultSet = this.get();

        while (resultSet.next()) {
            subCategories.add(this._toObject(resultSet));
        }

        return subCategories;
    }

    private ArrayList<SubCategory> _list(int typeId) throws SQLException {
        ArrayList<SubCategory> subCategories = new ArrayList<>();
        ResultSet resultSet = this.get("type_id = " + typeId);

        while (resultSet.next()) {
            subCategories.add(this._toObject(resultSet));
        }

        return subCategories;
    }

    private int _create(SubCategory subCategory) throws SQLException {
        String statementString = "INSERT INTO sub_categories(type_id, category_id, money_type, name, icon, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, subCategory.getTypeId());
        statement.setInt(2, subCategory.getCategoryId());
        statement.setString(3, subCategory.getMoneyType());
        statement.setString(4, subCategory.getName());
        statement.setString(5, subCategory.getIcon());
        statement.setTimestamp(6, this.getCurrentTime());
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private boolean _create(ArrayList<SubCategory> subCategories) throws SQLException {
        String statementString = "INSERT INTO sub_categories(type_id, category_id, money_type, name, icon, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        int i = 0;

        for (SubCategory subCategory : subCategories) {
            statement.setInt(1, subCategory.getTypeId());
            statement.setInt(2, subCategory.getCategoryId());
            statement.setString(3, subCategory.getMoneyType());
            statement.setString(4, subCategory.getName());
            statement.setString(5, subCategory.getIcon());
            statement.setTimestamp(6, this.getCurrentTime());
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == subCategories.size()) {
                statement.executeBatch(); // Execute every 1000 items.
            }
        }

        return true;
    }

    private int _update(SubCategory subCategory, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        state.setInt(2, id)
//        statement.setDouble(1, subCategory.getAmount());

        return statement.executeUpdate();
    }

    @Override
    protected SubCategory _toObject(ResultSet resultSet) throws SQLException {
        SubCategory subCategory = new SubCategory();
        subCategory.setId(resultSet.getInt("id"));
        subCategory.setTypeId(resultSet.getInt("type_id"));
        subCategory.setCategoryId(resultSet.getInt("category_id"));
        subCategory.setMoneyType(resultSet.getString("money_type"));
        subCategory.setName(resultSet.getString("name"));
        subCategory.setIcon(resultSet.getString("icon"));
        subCategory.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        subCategory.setUpdatedAt(this.getUpdatedAt(resultSet.getTimestamp("updated_at")));

        return subCategory;
    }
}
