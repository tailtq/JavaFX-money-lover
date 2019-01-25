package com.moneylover.app.Budget.View;

import com.jfoenix.controls.JFXPopup;
import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Infrastructure.Contracts.DialogInterface;
import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Helpers.DateHelper;
import com.moneylover.Modules.Budget.Controllers.BudgetController;
import com.moneylover.Modules.Budget.Entities.Budget;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.Budget.BudgetPresenter;
import com.moneylover.app.Category.CategoryPresenter;
import com.moneylover.app.Transaction.TransactionPresenter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class BudgetCell extends ListCell<Budget> implements DialogInterface {
    private BudgetController budgetController;

    private CategoryPresenter categoryPresenter;

    private Budget budget;

    private VBox budgetCell;

    private StringProperty handledBudgetId;

    private ObservableList<Wallet> wallets;

    public BudgetCell(StringProperty handledBudgetId) throws IOException, SQLException, ClassNotFoundException {
        this.handledBudgetId = handledBudgetId;
        this.budgetController = new BudgetController();
        this.categoryPresenter = new CategoryPresenter(this.selectedCategory, this.selectedSubCategory);
        this.loadCell();
    }

    private void loadCell() throws IOException {
        FXMLLoader budgetCellLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/budget/budget-cell.fxml"));
        budgetCellLoader.setController(this);
        this.budgetCell = budgetCellLoader.load();
    }

    public void setWallets(ObservableList<Wallet> wallets) {
        this.wallets = wallets;
    }

    /*========================== Draw ==========================*/
    @FXML
    private Button selectCategory;

    @FXML
    private MenuButton selectWallet;

    @FXML
    private TextField textFieldBudgetAmount;

    @FXML
    private DatePicker datePickerStartedAt;

    @FXML
    private DatePicker datePickerEndedAt;

    private IntegerProperty walletId = new SimpleIntegerProperty(0);

    private IntegerProperty selectedCategory = new SimpleIntegerProperty(0);

    private IntegerProperty selectedSubCategory = new SimpleIntegerProperty(0);

    @Override
    protected void updateItem(Budget item, boolean empty) {
        super.updateItem(item, empty);

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
    private void chooseCategory() throws IOException {
        this.categoryPresenter.showCategoryDialog();
    }

    @FXML
    private void edit() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/budget/budget-edit.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();
        this.loadBudgetData();
        this.createScreen(parent, "Edit Budget", 500, 170);
    }

    private void loadBudgetData() {
        this.walletId.set(this.budget.getWalletId());
        this.selectedCategory.set(0);
        this.selectedSubCategory.set(0);
        TransactionPresenter.loadStaticWallets(this.selectWallet, this.walletId, this.wallets);
        this.categoryPresenter.handleSelectedCategoryId(this.selectedCategory, this.selectCategory, "category");
        this.categoryPresenter.handleSelectedCategoryId(this.selectedSubCategory, this.selectCategory, "subCategory");
        this.textFieldBudgetAmount.setText(Float.toString(this.budget.getAmount()));
        this.datePickerStartedAt.setValue(LocalDate.parse(this.budget.getStartedAt().toString()));
        this.datePickerEndedAt.setValue(LocalDate.parse(this.budget.getEndedAt().toString()));

        if (this.budget.getBudgetableType().equals(CommonConstants.APP_SUB_CATEGORY)) {
            this.selectedSubCategory.set(this.budget.getBudgetableId());
        } else {
            this.selectedCategory.set(this.budget.getBudgetableId());
        }
    }

    @FXML
    private void updateBudget(Event event) {
        int walletId = this.walletId.get();
        int categoryId = this.selectedCategory.get();
        int subCategoryId = this.selectedSubCategory.get();
        String amountText = this.textFieldBudgetAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.trim());
        LocalDate startedAt = this.datePickerStartedAt.getValue();
        LocalDate endedAt = this.datePickerEndedAt.getValue();

        if (walletId == 0) {
            this.showErrorDialog("Wallet is not selected");
            return;
        }
        if (categoryId == 0 && subCategoryId == 0) {
            this.showErrorDialog("Category is not selected");
            return;
        }
        if (amount <= 0) {
            this.showErrorDialog("Amount is not valid");
            return;
        }
        if (DateHelper.isLaterThan(endedAt, startedAt)) {
            this.showErrorDialog("Budget time is not valid");
            return;
        }

        Budget budget = new Budget();
        budget.setWalletId(walletId);
        budget.setAmount(amount);
        budget.setSpentAmount(0);
        budget.setStartedAt(Date.valueOf(startedAt.toString()));
        budget.setEndedAt(Date.valueOf(endedAt.toString()));
        BudgetPresenter.addCategory(budget, categoryId, subCategoryId);

        try {
            int id = this.budget.getId();
            this.budgetController.update(budget, id);
            this.handledBudgetId.set("UPDATE-" + id);
            this.closeScene(event);
        } catch (SQLException | NotFoundException e) {
            e.printStackTrace();
        }
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

    @FXML
    public void closeScene(Event e) {
        Node node = (Node) e.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
