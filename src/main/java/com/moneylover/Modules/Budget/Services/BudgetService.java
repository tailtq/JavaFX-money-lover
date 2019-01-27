package com.moneylover.Modules.Budget.Services;

import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Budget.Entities.Budget;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.TimeZone;

public class BudgetService extends BaseService {
    public BudgetService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return Budget.getTable();
    }

    public ArrayList<Budget> list(int walletId) throws SQLException {
        ArrayList<Budget> budgets = this._list(walletId);

        return budgets;
    }

    public Budget getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getDetailByJoin(
                "budgets.*, categories.icon as category_icon, sub_categories.icon as sub_category_icon",
                "LEFT JOIN categories ON categories.id = " +
                        "CASE WHEN budgets.budgetable_type = '" + CommonConstants.APP_CATEGORY + "' " +
                        "THEN budgets.budgetable_id ELSE null END " +
                        "LEFT JOIN sub_categories ON sub_categories.id = " +
                        "CASE WHEN budgets.budgetable_type = '" + CommonConstants.APP_SUB_CATEGORY + "' " +
                        "THEN budgets.budgetable_id ELSE null END",
                "budgets.id = " + id
        );

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Budget budget = this.toObject(resultSet);
        this.closeStatement();

        return budget;
    }

    public Budget getDetail(String name) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getDetailBy("name = '" + name + "'");

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Budget budget = this.toObject(resultSet);
        this.closeStatement();

        return budget;
    }

    public Budget create(Budget budget) throws SQLException, NotFoundException {
        int id = this._create(budget);

        return this.getDetail(id);
    }

    public boolean create(ArrayList<Budget> budgets) throws SQLException {
        this._create(budgets);

        return true;
    }

    public Budget update(Budget budget, int id) throws SQLException, NotFoundException {
        this._update(budget, id);

        return this.getDetail(id);
    }

    /*====================================================================================*/
    private ArrayList<Budget> _list(int walletId) throws SQLException {
        ArrayList<Budget> budgets = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "budgets.*, categories.icon as category_icon, sub_categories.icon as sub_category_icon",
                "LEFT JOIN categories ON categories.id = " +
                        "CASE WHEN budgets.budgetable_type = '" + CommonConstants.APP_CATEGORY + "' " +
                        "THEN budgets.budgetable_id ELSE null END " +
                     "LEFT JOIN sub_categories ON sub_categories.id = " +
                        "CASE WHEN budgets.budgetable_type = '" + CommonConstants.APP_SUB_CATEGORY + "' " +
                        "THEN budgets.budgetable_id ELSE null END",
                "wallet_id = " + walletId
        );

        while (resultSet.next()) {
            budgets.add(this.toObject(resultSet));
        }

        return budgets;
    }

    private int _create(Budget budget) throws SQLException {
        String statementString = "INSERT INTO budgets(wallet_id, budgetable_id, budgetable_type, started_at, ended_at, amount, spent_amount, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = this.handleCreateProcess(budget, statementString);
        statement.setTimestamp(8, this.getCurrentTime());
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private boolean _create(ArrayList<Budget> budgets) throws SQLException {
        String statementString = "INSERT INTO budgets(wallet_id, budgetable_id, budgetable_type, started_at, ended_at, amount, spent_amount, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        int i = 0;

        for (Budget budget : budgets) {
            LocalDateTime currentDate = LocalDateTime.now().plusSeconds(i);
            statement.setInt(1, budget.getWalletId());
            statement.setInt(2, budget.getBudgetableId());
            statement.setString(3, budget.getBudgetableType());
            statement.setDate(4, Date.valueOf(budget.getStartedAt().toString()));
            statement.setDate(5, Date.valueOf(budget.getEndedAt().toString()));
            statement.setFloat(6, budget.getAmount());
            statement.setFloat(7, budget.getSpentAmount());
            statement.setTimestamp(8, Timestamp.valueOf(currentDate));
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == budgets.size()) {
                statement.executeBatch(); // Execute every 1000 items.
            }
        }

        this.closePreparedStatement();

        return true;
    }

    private boolean _update(Budget budget, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET wallet_id = ?, budgetable_id = ?, budgetable_type = ?, started_at = ?, ended_at = ?, amount = ?, updated_at = ? WHERE id = ?";
        PreparedStatement statement = this.handleCreateProcess(budget, statementString);
        statement.setTimestamp(7, this.getCurrentTime());
        statement.setInt(8, id);
        statement.executeUpdate();
        this.closePreparedStatement();

        return true;
    }

    private PreparedStatement handleCreateProcess(Budget budget, String statementString) throws SQLException {
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, budget.getWalletId());
        statement.setInt(2, budget.getBudgetableId());
        statement.setString(3, budget.getBudgetableType());
        statement.setDate(4, Date.valueOf(budget.getStartedAt().toString()));
        statement.setDate(5, Date.valueOf(budget.getEndedAt().toString()));
        statement.setFloat(6, budget.getAmount());

        if (statementString.contains("INSERT")) {
            statement.setFloat(7, budget.getSpentAmount());
        }

        return statement;
    }

    @Override
    protected Budget toObject(ResultSet resultSet) throws SQLException {
        Budget budget = new Budget();
        budget.setId(resultSet.getInt("id"));
        budget.setWalletId(resultSet.getInt("wallet_id"));
        budget.setBudgetableId(resultSet.getInt("budgetable_id"));
        budget.setBudgetableType(resultSet.getString("budgetable_type"));
        budget.setStartedAt(resultSet.getDate("started_at").toLocalDate());
        budget.setEndedAt(resultSet.getDate("ended_at").toLocalDate());
        budget.setAmount(resultSet.getFloat("amount"));
        budget.setSpentAmount(resultSet.getFloat("spent_amount"));
        budget.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        budget.setUpdatedAt(this.getUpdatedAt(resultSet.getTimestamp("updated_at")));

        if (budget.getBudgetableType().equals(CommonConstants.APP_SUB_CATEGORY)) {
            budget.setCategoryIcon(resultSet.getString("sub_category_icon"));
        } else {
            budget.setCategoryIcon(resultSet.getString("category_icon"));
        }

        return budget;
    }
}
