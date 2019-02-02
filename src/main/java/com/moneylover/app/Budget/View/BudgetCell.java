package com.moneylover.app.Budget.View;

import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Infrastructure.Contracts.DialogInterface;
import com.moneylover.Infrastructure.Helpers.CurrencyHelper;
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
import com.moneylover.app.Transaction.TransactionPresenter;
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
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BudgetCell extends ListCell<Budget> implements DialogInterface {
    private TransactionController transactionController;

    private CategoryPresenter categoryPresenter;

    private Budget budget;

    private VBox budgetCell;

    private StringProperty handledBudgetId;

    private ObservableList<Wallet> wallets;

    private IntegerProperty walletIndex;

    private ObservableList<Transaction> transactions;

    private LocalDate currentDate = LocalDate.now();

    public BudgetCell(StringProperty handledBudgetId) throws IOException {
        this.handledBudgetId = handledBudgetId;
        this._loadCell();
    }

    private void _loadCell() throws IOException {
        FXMLLoader budgetCellLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/budget/budget-cell.fxml"));
        budgetCellLoader.setController(this);
        this.budgetCell = budgetCellLoader.load();
    }

    public void setWallets(ObservableList<Wallet> wallets) {
        this.wallets = wallets;
    }

    public void setWalletIndex(IntegerProperty walletIndex) {
        this.walletIndex = walletIndex;
    }

    public Wallet getWallet() {
        return this.wallets.get(this.walletIndex.getValue());
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewTransactions;

    @FXML
    private Label
            labelBudgetTime,
            labelBudgetRemainingTime,
            labelBudgetAmount,
            labelBudgetRemainingAmount,
            labelBudgeSpentAmount,
            labelBudgetNormalDailyAmount,
            labelBudgetDailyAmount,
            labelBudgetStatus,
            labelBudgetDate,
            labelBudgetCategoryName;

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

    @FXML
    private Text textDialogTitle;

    private IntegerProperty
            walletId = new SimpleIntegerProperty(0),
            selectedCategory = new SimpleIntegerProperty(0),
            selectedSubCategory = new SimpleIntegerProperty(0);

    @Override
    protected void updateItem(Budget item, boolean empty) {
        super.updateItem(item, empty);
        this.budget = item;

        if (empty) {
            setGraphic(null);
            return;
        }

        String moneySymbol = this.getWallet().getMoneySymbol();
        LocalDate startedAt = item.getStartedAt(),
                endedAt = item.getEndedAt();
        long daysLeft = ChronoUnit.DAYS.between(this.currentDate, endedAt);
        daysLeft = daysLeft >= 1 ? daysLeft : 0;
        float remainingAmount = budget.getAmount() - budget.getSpentAmount();
        String startedAtText = startedAt.format(DateTimeFormatter.ofPattern("MM/dd/YYYY")),
                endedAtText = endedAt.format(DateTimeFormatter.ofPattern("MM/dd/YYYY")),
                imageUrl = "/assets/images/categories/" + item.getCategoryIcon() + ".png";
        this.labelBudgetRemainingTime.setText(daysLeft + (daysLeft > 1 ? " days" : " day") + " left");
        this.labelBudgetRemainingAmount.getStyleClass().removeAll("success-color", "danger-color");
        this.labelBudgetRemainingAmount.getStyleClass().add((remainingAmount > 0) ? "success-color" : "danger-color");
        this.labelBudgetRemainingAmount.setText(
                (remainingAmount > 0 ? "Left " : "Overspent ") + CurrencyHelper.toMoneyString(remainingAmount, moneySymbol)
        );
        this.labelBudgetTime.setText(startedAtText + " - " + endedAtText);
        this.labelBudgetAmount.setText(CurrencyHelper.toMoneyString(budget.getAmount(), moneySymbol));
        this.imageBudgetCategory.setImage(new Image(imageUrl));
        this.progressBarRemainingAmount.setProgress(budget.getSpentAmount() / budget.getAmount());
        setGraphic(this.budgetCell);
    }

    @FXML
    private void showPopup(Event e) throws IOException {
        this.addViewPopup((Node) e.getSource());
    }

    @FXML
    private void show() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/budget/budget-show.fxml")
        );
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();
        this._loadAreaChart();
        this._loadBudgetData();
        this.createScreen(parent, "Budget Detail", 470, 500);
    }

    private void _loadBudgetData() {
        long totalDays = Math.abs(ChronoUnit.DAYS.between(this.budget.getStartedAt(), this.budget.getEndedAt()));
        long passingDays = Math.abs(ChronoUnit.DAYS.between(this.budget.getStartedAt(), this.currentDate));
        String moneySymbol = this.getWallet().getMoneySymbol(),
                imageUrl = "/assets/images/categories/" + this.budget.getCategoryIcon() + ".png";

        if (this._isOverspent(budget)) {
            this.labelBudgetStatus.setText("Overspent");
            this.labelBudgetRemainingAmount.getStyleClass().add("danger-color");
        } else {
            this.labelBudgetStatus.setText("Left");
        }

        this.imageBudgetCategory.setImage(new Image(imageUrl));
        this.labelBudgetDate.setText(
                budget.getStartedAt().format(DateHelper.getFormat()) + " - " + budget.getEndedAt().format(DateHelper.getFormat())
        );
        this.labelBudgetCategoryName.setText(budget.getCategoryName());
        this.labelBudgetAmount.setText(CurrencyHelper.toMoneyString(budget.getAmount(), moneySymbol));
        this.labelBudgetRemainingAmount.setText(CurrencyHelper.toMoneyString(budget.getAmount() - budget.getSpentAmount(), moneySymbol));
        this.labelBudgeSpentAmount.setText(CurrencyHelper.toMoneyString(budget.getSpentAmount(), moneySymbol));
        this.labelBudgetNormalDailyAmount.setText(CurrencyHelper.toMoneyString(budget.getAmount() / totalDays, moneySymbol) + " / day");
        this.labelBudgetDailyAmount.setText(CurrencyHelper.toMoneyString(budget.getSpentAmount() / (passingDays == 0 ? 1 : passingDays), moneySymbol));
    }

    private void _loadAreaChart() throws SQLException, ClassNotFoundException {
        this.transactions = FXCollections.observableArrayList((new TransactionController()).listByBudget(this.budget));
        ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions = FXCollections.observableArrayList();
        ReportPresenter.sortTransactionsByDate(transactions, this.transactions, this.getWallet().getMoneySymbol());
        this._loadAreaChartData(transactions, this.budget);
    }

    private void _loadAreaChartData(ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions, Budget budget) {
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
        ReportPresenter.listTransactions(this.listViewTransactions, this.transactions, this.getWallet());
        Budget budget = this.budget;
        String startedAt = budget.getStartedAt().format(DateHelper.getFormat()),
                endedAt = budget.getEndedAt().format(DateHelper.getFormat());
        this.textDialogTitle.setText("Transactions from " + startedAt + " to " + endedAt);
        this.createScreen(parent, "Budget Detail", 470, 480);
    }

    @FXML
    private void chooseCategory() throws IOException {
        this.categoryPresenter.showCategoryDialog();
    }

    @FXML
    private void edit() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/budget/budget-save.fxml")
        );
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();
        this.categoryPresenter = new CategoryPresenter(this.selectedCategory, this.selectedSubCategory);
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
        this.textFieldBudgetAmount.setText(CurrencyHelper.toMoney(this.budget.getAmount()));
        this.datePickerStartedAt.setValue(this.budget.getStartedAt());
        this.datePickerEndedAt.setValue(this.budget.getEndedAt());

        if (this.budget.getBudgetableType().equals(CommonConstants.APP_SUB_CATEGORY)) {
            this.selectedSubCategory.set(this.budget.getBudgetableId());
        } else {
            this.selectedCategory.set(this.budget.getBudgetableId());
        }
    }

    @FXML
    private void changeAmount() {
        TransactionPresenter.parseTextFieldMoney(this.textFieldBudgetAmount);
    }

    @FXML
    private void saveBudget(Event event) {
        int walletId = this.walletId.get();
        int categoryId = this.selectedCategory.get();
        int subCategoryId = this.selectedSubCategory.get();
        String amountText = this.textFieldBudgetAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.replaceAll("[^\\d.]", ""));
        LocalDate startedAt = this.datePickerStartedAt.getValue();
        LocalDate endedAt = this.datePickerEndedAt.getValue();
        String validation = BudgetPresenter.validateBudget(walletId, categoryId, subCategoryId, amount, startedAt, endedAt);

        if (validation != null) {
            this.showErrorDialog(validation);
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
            (new BudgetController()).update(budget, id);
            this.handledBudgetId.set(null);
            this.handledBudgetId.set("UPDATE-" + id);
            this.closeScene(event);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }

    @FXML
    private void delete() {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            try {
                int id = this.budget.getId();
                (new BudgetController()).delete(id);
                this.handledBudgetId.set("DELETE-" + id);
            } catch (SQLException | ClassNotFoundException e1) {
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
        DialogInterface.closeScene(e);
    }
}
