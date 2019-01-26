package com.moneylover.app.Report;

import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Infrastructure.Helpers.DateHelper;
import com.moneylover.Modules.Category.Entities.Category;
import com.moneylover.Modules.Time.Entities.CustomDate;
import com.moneylover.Modules.Transaction.Controllers.TransactionController;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.PagePresenter;
import com.moneylover.app.Report.View.ReportCell;
import com.moneylover.app.Transaction.TransactionPresenter;
import com.moneylover.app.Transaction.View.TransactionDate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Iterator;

public class ReportPresenter extends PagePresenter {
    private TransactionController transactionController;

    private LocalDate startDate;

    private LocalDate endDate;

    private ArrayList<Transaction> transactions;

    private ObservableList<Pair<CustomDate, ObservableList<Transaction>>> monthTransactions = FXCollections.observableArrayList();

    private ObservableList<Pair<Category, ObservableList<Transaction>>> inflowTransactions = FXCollections.observableArrayList();

    private ObservableList<Pair<Category, ObservableList<Transaction>>> outflowTransactions = FXCollections.observableArrayList();

    public ReportPresenter() throws SQLException, ClassNotFoundException {
        this.startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        this.endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        this.transactionController = new TransactionController();
    }

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException {
        super.setWallets(wallets);
        Wallet wallet = this.wallets.get(0);
        this.transactions = this.loadTransactions(wallet.getId());
        this._loadBarChart(wallet.getMoneySymbol(), this.startDate, this.endDate);
        this._loadPieCharts();
    }

    private ArrayList<Transaction> loadTransactions(int walletId) throws SQLException {
        return this.transactionController.listByDateRange(walletId, startDate, endDate);
    }

    private void _loadBarChart(
            String moneySymbol,
            LocalDate startDate,
            LocalDate endDate
    ) {
        ArrayList transactions = (ArrayList) this.transactions.clone();

        if (DateHelper.isSameMonth(startDate, endDate)) {
            TransactionPresenter.sortTransactionsByDate(this.monthTransactions, transactions, moneySymbol);
        } else {
            ReportPresenter._sortTransactionsByMonth(this.monthTransactions, transactions, moneySymbol);
        }

        this._loadBarChartData(this.monthTransactions, startDate, endDate);
    }

    private static void _sortTransactionsByMonth(
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> sortedTransactions,
            ArrayList<Transaction> transactions,
            String moneySymbol
    ) {
        sortedTransactions.clear();

        for (Iterator<Transaction> it = transactions.iterator(); it.hasNext();) {
            Transaction transaction = it.next();
            boolean hasMonth = false;
            LocalDate localDate = LocalDate.parse(transaction.getTransactedAt().toString());
            int month = localDate.getMonthValue();

            for (Pair<CustomDate, ObservableList<Transaction>> pair: sortedTransactions) {
                if (pair.getKey().getMonthNumber() == month) {
                    pair.getValue().add(transaction);
                    it.remove();
                    hasMonth = true;
                    break;
                }
            }

            if (!hasMonth) {
                TransactionPresenter.addNewDay(sortedTransactions, transaction, localDate, moneySymbol);
                it.remove();
            }
        }
    }

    private void _loadPieCharts() {
        ReportPresenter._sortTransactionByCategories(this.inflowTransactions, this.outflowTransactions, this.transactions);
        this._loadPieChartData(this.incomeChart, this.inflowTransactions);
        this._loadPieChartData(this.expenseChart, this.outflowTransactions);

    }

    private static void _sortTransactionByCategories(
            ObservableList<Pair<Category, ObservableList<Transaction>>> inflowTransactions,
            ObservableList<Pair<Category, ObservableList<Transaction>>> outflowTransactions,
            ArrayList<Transaction> transactions
    ) {
        for (Iterator<Transaction> it = transactions.iterator(); it.hasNext();) {
            Transaction transaction = it.next();
            String moneyType = transaction.getCategoryMoneyType();
            boolean hasCategory = false;

            if (moneyType.equals(CommonConstants.INCOME) || moneyType.equals(CommonConstants.EXPENSE)) {
                boolean isEqualIncome = moneyType.equals(CommonConstants.INCOME);

                for (Pair<Category, ObservableList<Transaction>> flowTransaction: (isEqualIncome ? inflowTransactions : outflowTransactions)) {
                    if (transaction.getCategoryId() == flowTransaction.getKey().getId()) {
                        flowTransaction.getValue().add(transaction);
                        hasCategory = true;
                        it.remove();
                        break;
                    }
                }

                if (!hasCategory) {
                    ReportPresenter.addNewCategory((isEqualIncome ? inflowTransactions : outflowTransactions), transaction);
                }
            }
        }
    }

    public static void addNewCategory(
            ObservableList<Pair<Category, ObservableList<Transaction>>> transactions,
            Transaction newTransaction
    ) {
        Category newCategory = new Category();
        newCategory.setId(newTransaction.getCategoryId());
        newCategory.setName(newTransaction.getCategoryName());
        newCategory.setIcon(newTransaction.getCategoryIcon());
        newCategory.setMoneyType(newTransaction.getCategoryMoneyType());
        transactions.add(new Pair<>(newCategory, FXCollections.observableArrayList(newTransaction)));
    }

    /*========================== Draw ==========================*/
    @FXML
    private BarChart dateRangeChart;

    @FXML
    private PieChart expenseChart;

    @FXML
    private PieChart incomeChart;

    @FXML
    private ListView listViewMonthTransactions;

    @FXML
    private ListView listViewMonthTransactionsDetail;

    @FXML
    private DatePicker datePickerStartDate;

    @FXML
    private DatePicker datePickerEndDate;

    @FXML
    private Text title;

    public void loadPresenter() {
        // TODO: Calculate data
    }

    private void _loadBarChartData(
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions,
            LocalDate startDate,
            LocalDate endDate
    ) {
        this.dateRangeChart.getData().clear();
        ObservableList<String> titles = FXCollections.observableArrayList();
        CategoryAxis categoryAxis = (CategoryAxis) this.dateRangeChart.getXAxis();
        String dateRangeType = this._getDateRange(startDate, endDate);
        XYChart.Series inflowSeries = new XYChart.Series<>();
        XYChart.Series outflowSeries = new XYChart.Series();
        inflowSeries.setName("Inflow");
        outflowSeries.setName("Outflow");

        for (int i = transactions.size() - 1; i >= 0; i--) {
            Pair<CustomDate, ObservableList<Transaction>> transaction = transactions.get(i);
            CustomDate customDate = transaction.getKey();
            String title;
            float inflow = 0;
            float outflow = 0;

            switch (dateRangeType) {
                case CommonConstants.DAY_RANGE:
                    title = Integer.toString(customDate.getDayOfMonth());
                    break;
                case (CommonConstants.MONTH_RANGE):
                    title = Integer.toString(customDate.getMonthNumber());
                    break;
                default:
                    title = customDate.getMonthNumber() + "/" + customDate.getYear();
                    break;
            }

            for (Transaction transactionItem: transaction.getValue()) {
                float amount = transactionItem.getAmount();

                if (amount > 0) {
                    inflow += amount;
                } else {
                    outflow += amount;
                }
            }

            titles.add(title);
            inflowSeries.getData().add(new XYChart.Data<>(title, inflow));
            outflowSeries.getData().add(new XYChart.Data<>(title, outflow));
        }

        categoryAxis.setCategories(titles);
        this.dateRangeChart.getData().addAll(inflowSeries, outflowSeries);
    }

    private String _getDateRange(LocalDate startDate, LocalDate endDate) {
        if (DateHelper.isSameMonth(startDate, endDate)) {
            return CommonConstants.DAY_RANGE;
        } else if (DateHelper.isSameYear(startDate, endDate)) {
            return CommonConstants.MONTH_RANGE;
        } else {
            return CommonConstants.YEAR_RANGE;
        }
    }

    private void _loadPieChartData(
            PieChart pieChart,
            ObservableList<Pair<Category, ObservableList<Transaction>>> transactions
    ) {
        pieChart.getData().clear();

        for (Pair<Category, ObservableList<Transaction>> transaction: transactions) {
            float value = 0;

            for (Transaction transactionItem: transaction.getValue()) {
                value += transactionItem.getAmount();
            }

            pieChart.getData().add(new PieChart.Data(transaction.getKey().getName(), Math.abs(value)));
        }
    }

    @FXML
    private void loadMonthTransactions() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/report/report-months.fxml")
        );
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        Wallet wallet = this.wallets.get(0);
        this._loadBarChart(wallet.getMoneySymbol(), this.startDate, this.endDate);
        this._listMonthTransactions();
        this.dateRangeChart.setTitle("Report");
        Stage stage = this.createScreen(parent, "Report", 500, 700);
    }

    private void _listMonthTransactions() {
        this.listViewMonthTransactions.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<Pair<CustomDate, ObservableList<Transaction>>>() {
                @Override
                public void changed(
                        ObservableValue<? extends Pair<CustomDate, ObservableList<Transaction>>> observable,
                        Pair<CustomDate, ObservableList<Transaction>> oldValue,
                        Pair<CustomDate, ObservableList<Transaction>> newValue
                ) {
                    try {
                        ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions = FXCollections.observableArrayList();
                        ArrayList<Transaction> monthTransactions = new ArrayList<>(newValue.getValue());
                        String moneySymbol = wallets.get(0).getMoneySymbol();
                        TransactionPresenter.sortTransactionsByDate(transactions, monthTransactions, moneySymbol);

                        _loadMonthTransactionsDetail(transactions);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        );
        this.listViewMonthTransactions.setItems(this.monthTransactions);
        this.listViewMonthTransactions.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                try {
                    return new ReportCell();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    @FXML
    private void _loadMonthTransactionsDetail(
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions
    ) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/report/report-detail.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        ReportPresenter.listTransactions(this.listViewMonthTransactionsDetail, transactions);

        this.createScreen(parent, "Report Detail", 400, 500);
    }

    public static void listTransactions(
            ListView listView,
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions
    ) {
        listView.setItems(transactions);
        listView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                try {
                    TransactionDate transactionDate =  new TransactionDate();
                    transactionDate.setDisableOptions(true);

                    return transactionDate;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    @FXML
    private void changeDayRange() throws SQLException {
        LocalDate startDate = this.datePickerStartDate.getValue();
        LocalDate endDate = this.datePickerEndDate.getValue();

        if (startDate != null && endDate != null
                && (!DateHelper.isSameDay(this.startDate, startDate) || !DateHelper.isSameDay(this.endDate, endDate))) {
            this.startDate = startDate;
            this.endDate = endDate;

            Wallet wallet = this.wallets.get(0);
            this.transactions = this.loadTransactions(wallet.getId());
            this._loadBarChart(wallet.getMoneySymbol(), this.startDate, this.endDate);
            this._loadPieCharts();
        }
    }
}
