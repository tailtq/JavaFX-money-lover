package com.moneylover.app.Budget.View;

import com.jfoenix.controls.JFXPopup;
import com.moneylover.Infrastructure.Contracts.DialogInterface;
import com.moneylover.Modules.Budget.Controllers.BudgetController;
import com.moneylover.Modules.Budget.Entities.Budget;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class BudgetCell extends ListCell<Budget> implements DialogInterface {
    private BudgetController budgetController;

    private Budget budget;

    private VBox budgetCell;

    private StringProperty handledBudgetId;

    public BudgetCell(StringProperty handledBudgetId) throws IOException, SQLException, ClassNotFoundException {
        this.handledBudgetId = handledBudgetId;
        this.budgetController = new BudgetController();
        this.loadCell();
    }

    private void loadCell() throws IOException {
        FXMLLoader budgetCellLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/budget/budget-cell.fxml"));
        budgetCellLoader.setController(this);
        this.budgetCell = budgetCellLoader.load();
    }

    /*========================== Draw ==========================*/
    @Override
    protected void updateItem(Budget item, boolean empty) {
        if (empty) {
            setGraphic(null);
            return;
        }

        this.budget = item;
        setGraphic(this.budgetCell);
    }

    @FXML
    private void showPopup(Event e) throws IOException {
        Node button = (Node) e.getSource();
        FXMLLoader optionalButtonsLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/optional-buttons.fxml"));
        optionalButtonsLoader.setController(this);
        HBox container = optionalButtonsLoader.load();

        JFXPopup popup = new JFXPopup(container);
        popup.show(button, JFXPopup.PopupVPosition.BOTTOM, JFXPopup.PopupHPosition.LEFT, 30, 10);
    }

    @FXML
    private void edit() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/budget/budget-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        this.createScreen(parent, "Edit Budget", 500, 170);
    }

    @FXML
    private void delete() {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            try {
                int id = this.budget.getId();
                this.budgetController.delete(id);
                this.handledBudgetId.set("DELETE-" + id);
            } catch (SQLException e1) {
                e1.printStackTrace();
                this.showErrorDialog("An error has occurred");
            }
        }
    }
}
