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

    public SubCategory getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);

        if (resultSet.wasNull()) {
            throw new NotFoundException();
        }

        SubCategory subCategory = this.toObject(resultSet);
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
            subCategories.add(this.toObject(resultSet));
        }

        return subCategories;
    }

    private int _create(SubCategory subCategory) throws SQLException {
        String statementString = "INSERT INTO sub_categories(money_type, name, created_at) VALUES (?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);

        LocalDate currentDate = LocalDate.now();
        statement.setString(1, subCategory.getMoneyType());
        statement.setString(2, subCategory.getName());
        statement.setDate(3, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));

        return statement.executeUpdate();
    }

    private boolean _create(ArrayList<SubCategory> subCategories) throws SQLException {
        String statementString = "INSERT INTO sub_categories(money_type, name, created_at) VALUES (?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        int i = 0;

        for (SubCategory subCategory : subCategories) {
            LocalDate currentDate = LocalDate.now();
            statement.setString(1, subCategory.getMoneyType());
            statement.setString(2, subCategory.getName());
            statement.setDate(3, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));
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
    protected SubCategory toObject(ResultSet resultSet) throws SQLException {
        SubCategory subCategory = new SubCategory();
        subCategory.setId(resultSet.getInt("id"));
        subCategory.setTypeId(resultSet.getInt("type_id"));
        subCategory.setCategoryId(resultSet.getInt("category_id"));
        subCategory.setMoneyType(resultSet.getString("money_type"));
        subCategory.setName(resultSet.getString("name"));
        subCategory.setIcon(resultSet.getString("icon"));
        subCategory.setCreatedAt(resultSet.getDate("created_at"));
        subCategory.setUpdatedAt(resultSet.getDate("updated_at"));

        return subCategory;
    }
}
