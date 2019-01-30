package com.moneylover.Modules.Budget.Controllers;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Budget.Entities.Budget;
import com.moneylover.Modules.Budget.Services.BudgetService;

import java.sql.SQLException;
import java.util.ArrayList;

public class BudgetController {
    private BudgetService service;

    public BudgetController() throws SQLException, ClassNotFoundException {
        service = new BudgetService();
    }

    public ArrayList<Budget> list(int walletId) throws SQLException {
        ArrayList<Budget> budgets = this.service.list(walletId);

        return budgets;
    }

    public Budget getDetail(int id) throws SQLException, NotFoundException {
        Budget budget = this.service.getDetail(id);

        return budget;
    }

    public Budget getDetail(String name) throws SQLException, NotFoundException {
        Budget budget = this.service.getDetail(name);

        return budget;
    }

    public Budget create(Budget budget) throws SQLException, NotFoundException, ClassNotFoundException {
        Budget newBudget = this.service.create(budget);

        return newBudget;
    }

    public boolean create(ArrayList<Budget> budgets) throws SQLException, NotFoundException {
        this.service.create(budgets);

        return true;
    }

    public boolean update(Budget budget, int id) throws SQLException, ClassNotFoundException {
        this.service.update(budget, id);

        return true;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }
}
