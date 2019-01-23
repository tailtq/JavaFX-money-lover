package com.moneylover.app.Budget.View;

import com.moneylover.Modules.Budget.Entities.Budget;
import com.moneylover.Modules.Time.Entities.CustomDateRange;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.util.Pair;

public class BudgetCell extends ListCell<Budget> {


    public BudgetCell() {

    }

    @Override
    protected void updateItem(Budget budget, boolean empty) {
        if (empty) {
            setGraphic(null);
        } else {
//            setGraphic();
        }
    }
}
