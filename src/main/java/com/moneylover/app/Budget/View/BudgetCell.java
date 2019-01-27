package com.moneylover.app.Budget.View;

import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Infrastructure.Contracts.DialogInterface;
import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Infrastructure.Helpers.DateHelper;
import com.moneylover.Modules.Budget.Controllers.BudgetController;
import com.moneylover.Modules.Budget.Entities.Budget;
import com.moneylover.Modules.Time.Entities.CustomDate;
import com.moneylover.Modules.Transaction.Controllers.TransactionController;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.Budget.BudgetPresenter;
import com.moneylover.app.Category.CategoryPresenter;
import com.moneylover.app.PagePresenter;
import com.moneylover.app.Report.ReportPresenter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BudgetCell extends ListCell<Budget> implements DialogInterface {
    private BudgetController budgetController;

    private TransactionController transactionController;

    private CategoryPresenter categoryPresenter;

    private Budget budget;

    private VBox budgetCell;

    private StringProperty handledBudgetId;

    private ObservableList<Wallet> wallets;

    private ObservableList<Transaction> transactions;

    private LocalDate currentDate = LocalDate.now();

    public BudgetCell(StringProperty handledBudgetId) throws IOException, SQLException, ClassNotFoundException {
        this.handledBudgetId = handledBudgetId;
        this.budgetController = new BudgetController();
        this.transactionController = new TransactionController();
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
    private ListView listViewTransactions;

    @FXML
    private Label labelBudgetTime,
            labelBudgetRemainingTime,
            labelBudgetAmount,
            labelBudgetRemainingAmount,
            labelBudgetDetailRemainingAmount,
            labelBudgetDetailSpentAmount,
            labelBudgetDetailRecommendedDailyAmount,
            labelBudgetDetailActualDailyAmount,
            labelBudgetDetailStatus;

    @FXML
    private ProgressBar progressBarRemainingAmount;

    @FXML
    private ImageView imageBudgetCategory;

    @FXML
    private AreaChart areaChartDetail;

    @FXML
    private Button selectCategory;

    @FXML
    private MenuButton selectWallet;

    @FXML
    private TextField textFieldBudgetAmount;

    @FXML
    private DatePicker datePickerStartedAt, datePickerEndedAt;

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
        String moneySymbol = this.wallets.get(0).getMoneySymbol();
        LocalDate startedAt = item.getStartedAt(),
                endedAt = item.getEndedAt();
        long daysLeft = Math.abs(ChronoUnit.DAYS.between(this.currentDate, endedAt));
        String startedAtText = startedAt.format(DateTimeFormatter.ofPattern("MM/dd/YYYY"));
        String endedAtText = endedAt.format(DateTimeFormatter.ofPattern("MM/dd/YYYY"));
        float remainingAmount = budget.getAmount() - budget.getSpentAmount();
        String imageUrl = "/assets/images/categories/" + item.getCategoryIcon() + ".png";

        this.labelBudgetTime.setText(startedAtText + " - " + endedAtText);
        this.labelBudgetRemainingTime.setText(daysLeft + (daysLeft > 1 ? " days" : " day") + " left");
        this.progressBarRemainingAmount.setProgress(budget.getSpentAmount() / budget.getAmount());
        this.labelBudgetAmount.setText("+" + budget.getAmount() + moneySymbol);
        this.labelBudgetRemainingAmount.setText((remainingAmount > 0 ? "Left +" : "") + remainingAmount + moneySymbol);
        this.imageBudgetCategory.setImage(new Image(imageUrl));
        setGraphic(this.budgetCell);
    }

    @FXML
    private void showPopup(Event e) throws IOException {
        this.addViewPopup((Node) e.getSource());
    }

    @FXML
    private void show() throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/budget/budget-show.fxml")
        );
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();

        this._loadAreaChart();
        this._loadBudgetData();
        this.createScreen(parent, "Budget Detail", 400, 500);
    }

    private void _loadBudgetData() {
        long totalDays = Math.abs(ChronoUnit.DAYS.between(this.budget.getStartedAt(), this.budget.getEndedAt()));
        long passingDays = Math.abs(ChronoUnit.DAYS.between(this.budget.getStartedAt(), this.currentDate));

        if (this._isOverspent(budget)) {
            this.labelBudgetDetailStatus.setText("Overspent");
            this.labelBudgetDetailRemainingAmount.getStyleClass().add("danger-color");
        }

        this.labelBudgetDetailRemainingAmount.setText(Float.toString(budget.getAmount() - budget.getSpentAmount()));
        this.labelBudgetDetailSpentAmount.setText(Float.toString(budget.getSpentAmount()));
        this.labelBudgetDetailRecommendedDailyAmount.setText(Float.toString(budget.getAmount() / totalDays));
        this.labelBudgetDetailActualDailyAmount.setText(Float.toString(budget.getSpentAmount() / (passingDays == 0 ? 1 : passingDays)));
    }

    private void _loadAreaChart() throws SQLException {
        this.transactions = FXCollections.observableArrayList();
        Wallet wallet = this.wallets.get(0);
        transactions.addAll(this.transactionController.listByBudget(this.budget));
//        this._loadAreaChartData(this.transactions, this.budget);
    }

    private void _loadAreaChartData( ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions, Budget budget) {
        this.areaChartDetail.getData().clear();
        XYChart.Series series = new XYChart.Series();

        for (int i = transactions.size() - 1; i >= 0; i--) {
            Pair<CustomDate, ObservableList<Transaction>> transaction = transactions.get(i);
            CustomDate customDate = transaction.getKey();
            String title;
            float amount = 0;

            if (DateHelper.isSameYear(budget.getStartedAt(), budget.getStartedAt())) {
                title = Integer.toString(customDate.getDayOfMonth()) + "/" + Integer.toString(customDate.getMonthNumber());
            } else {
                title = Integer.toString(customDate.getDayOfMonth()) + "/" + Integer.toString(customDate.getMonthNumber()) + "/" + Integer.toString(customDate.getYear());
            }

            for (Transaction transactionItem: transaction.getValue()) {
                amount += transactionItem.getAmount();
            }

            series.getData().add(new XYChart.Data<>(title, amount));
        }

        this.areaChartDetail.getData().add(series);
    }

    @FXML
    private void loadBudgetTransactions() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/budget/budget-detail.fxml")
        );
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        ReportPresenter.listTransactions(this.listViewTransactions, this.transactions);
        this.createScreen(parent, "Budget Detail", 400, 500);
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
        PagePresenter.loadStaticWallets(this.selectWallet, this.walletId, this.wallets);
        this.categoryPresenter.handleSelectedCategoryId(this.selectedCategory, this.selectCategory, "category");
        this.categoryPresenter.handleSelectedCategoryId(this.selectedSubCategory, this.selectCategory, "subCategory");
        this.textFieldBudgetAmount.setText(Float.toString(this.budget.getAmount()));
        this.datePickerStartedAt.setValue(this.budget.getStartedAt());
        this.datePickerEndedAt.setValue(this.budget.getEndedAt());

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
        budget.setStartedAt(startedAt);
        budget.setEndedAt(endedAt);
        BudgetPresenter.addCategory(budget, categoryId, subCategoryId);

        try {
            int id = this.budget.getId();
            this.budgetController.update(budget, id);
            this.handledBudgetId.set(null);
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

    private boolean _isOverspent(Budget budget) {
        return budget.getSpentAmount() > budget.getAmount();
    }

    @FXML
    public void closeScene(Event e) {
        Node node = (Node) e.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
