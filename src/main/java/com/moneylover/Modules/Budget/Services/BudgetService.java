package com.moneylover.Modules.Budget.Services;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Services.BaseService;
import com.moneylover.Modules.Budget.Entities.Budget;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

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
        ResultSet resultSet = this._getById(id);

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
        ResultSet resultSet = this.get("wallet_id = " + walletId);

        while (resultSet.next()) {
            budgets.add(this.toObject(resultSet));
        }

        return budgets;
    }

    private int _create(Budget budget) throws SQLException {
        String statementString = "INSERT INTO budgets(wallet_id, budgetable_id, budgetable_type, started_at, ended_at, amount, spent_amount, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);

        LocalDate currentDate = LocalDate.now();
        statement.setInt(1, budget.getWalletId());
        statement.setInt(2, budget.getBudgetableId());
        statement.setString(3, budget.getBudgetableType());
        statement.setDate(4, Date.valueOf(budget.getStartedAt().toString()));
        statement.setDate(5, Date.valueOf(budget.getEndedAt().toString()));
        statement.setFloat(6, budget.getAmount());
        statement.setFloat(7, budget.getSpentAmount());
        statement.setDate(8, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));

        return statement.executeUpdate();
    }

    private boolean _create(ArrayList<Budget> budgets) throws SQLException {
        String statementString = "INSERT INTO budgets(type_id, money_type, name, icon, created_at) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        int i = 0;

        for (Budget budget : budgets) {
            LocalDate currentDate = LocalDate.now();
            statement.setInt(1, budget.getWalletId());
            statement.setInt(2, budget.getBudgetableId());
            statement.setString(3, budget.getBudgetableType());
            statement.setDate(4, Date.valueOf(budget.getStartedAt().toString()));
            statement.setDate(5, Date.valueOf(budget.getEndedAt().toString()));
            statement.setFloat(6, budget.getAmount());
            statement.setFloat(7, budget.getSpentAmount());
            statement.setDate(8, new Date(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == budgets.size()) {
                statement.executeBatch(); // Execute every 1000 items.
            }
        }

        return true;
    }

    private int _update(Budget budget, int id) throws SQLException {
        String statementString = "UPDATE budgets SET created_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        // Continue
//        state.setInt(2, id)
//        statement.setDouble(1, budget.getAmount());

        return statement.executeUpdate();
    }

    @Override
    protected Budget toObject(ResultSet resultSet) throws SQLException {
        Budget budget = new Budget();

        return budget;
    }
}
