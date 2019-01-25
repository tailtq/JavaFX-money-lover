package com.moneylover.app.Budget.View;

import com.moneylover.Modules.Budget.Entities.Budget;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class BudgetCell extends ListCell<Budget> {
    private VBox budgetCell;

    public BudgetCell() throws IOException {
        FXMLLoader budgetCellLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/budget/budget-cell.fxml"));
        budgetCellLoader.setController(this);
        this.budgetCell = budgetCellLoader.load();
    }

    @Override
    protected void updateItem(Budget budget, boolean empty) {
        if (empty) {
            setGraphic(null);
            return;
        }

        setGraphic(this.budgetCell);
    }
}
