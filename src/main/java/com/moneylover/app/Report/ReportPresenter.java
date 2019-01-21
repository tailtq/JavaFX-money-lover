package com.moneylover.app.Report;

import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Modules.Time.Entities.CustomDate;
import com.moneylover.Modules.Transaction.Controllers.TransactionController;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.PagePresenter;
import com.moneylover.app.Transaction.TransactionPresenter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.text.Text;
import javafx.util.Pair;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

public class ReportPresenter extends PagePresenter {
    private TransactionController transactionController;

    private LocalDate startDate;

    private LocalDate endDate;

    private ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions = FXCollections.observableArrayList();

    public ReportPresenter() throws SQLException, ClassNotFoundException {
        this.startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        this.endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        this.transactionController = new TransactionController();
    }

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException {
        super.setWallets(wallets);
        this.getTransactionsByDayRange(this.wallets.get(0), this.startDate, this.endDate);
        this.loadBarChartData(this.transactions, startDate, endDate);
//        this.loadPieChartData(this.expenseChart, values);
//        this.loadPieChartData(this.incomeChart, values);
    }

    public void getTransactionsByDayRange(
            Wallet wallet,
            LocalDate startDate,
            LocalDate endDate
    ) throws SQLException {
        ArrayList<Transaction> transactions = this.transactionController.listByDateRange(wallet.getId(), startDate, endDate);

        if (startDate.getMonthValue() == endDate.getMonthValue()
                && startDate.getYear() == endDate.getYear()) {
            TransactionPresenter.sortTransactionsByDate(
                    this.transactions,
                    transactions,
                    wallet.getMoneySymbol()
            );
        }

        ReportPresenter.sortTransactionsByMonth(this.transactions, transactions, wallet.getMoneySymbol());
    }

    private static void sortTransactionsByMonth(
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> sortedTransactions,
            ArrayList<Transaction> transactions,
            String moneySymbol
    ) {
        for (Transaction transaction: transactions) {
            boolean hasMonth = false;
            LocalDate localDate = LocalDate.parse(transaction.getTransactedAt().toString());
            int month = localDate.getMonthValue();

            for (Pair<CustomDate, ObservableList<Transaction>> pair: sortedTransactions) {
                if (pair.getKey().getMonthNumber() == month) {
                    pair.getValue().add(transaction);
                    hasMonth = true;
                }
            }

            if (!hasMonth) {
                TransactionPresenter.addNewDay(sortedTransactions, transaction, localDate, moneySymbol);
            }
        }
    }

    /*========================== Draw ==========================*/
    @FXML
    private BarChart barChart;

    @FXML
    private BarChart barChartDetail;

    @FXML
    private PieChart expenseChart;

    @FXML
    private PieChart incomeChart;

    @FXML
    private PieChart pieChartDetail;

    @FXML
    private Text title;

    public void loadPresenter() {
        // TODO: Calculate data
    }

    public void loadBarChartData(
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions,
            LocalDate startDate,
            LocalDate endDate
    ) {
        String dateRangeType = this._getDateRange(startDate, endDate);
        XYChart.Series inflowSeries = new XYChart.Series();
        XYChart.Series outflowSeries = new XYChart.Series();
        inflowSeries.setName("Inflow");
        outflowSeries.setName("Outflow");
//        ObservableList inflowList = inflowSeries.getData();
//        ObservableList outflowList = outflowSeries.getData();

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
                System.out.println(amount);

                if (amount > 0) {
                    inflow += amount;
                } else {
                    outflow += amount;
                }
            }

            inflowSeries.getData().add(new XYChart.Data(title, inflow));
            outflowSeries.getData().add(new XYChart.Data(title, outflow));
        }

        this.barChart.getData().addAll(inflowSeries, outflowSeries);
    }

    private String _getDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.getMonthValue() == endDate.getMonthValue() && startDate.getYear() == startDate.getYear()) {
            return CommonConstants.DAY_RANGE;
        } else if (startDate.getYear() == endDate.getYear()) {
            return CommonConstants.MONTH_RANGE;
        } else {
            return CommonConstants.YEAR_RANGE;
        }
    }

    public void loadPieChartData(PieChart pieChart, ArrayList<Pair<String, Integer>> values) {
        ObservableList data = pieChart.getData();

        for (Pair<String, Integer> value: values) {
            PieChart.Data category = new PieChart.Data(value.getKey(), value.getValue());
            data.add(category);
        }
    }

    @FXML
    public void loadMonths() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/report/report-months.fxml")
        );
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        XYChart.Series clonalData = this.cloneBarChartData(this.barChart);
        this.barChartDetail.getData().add(clonalData);
        this.barChartDetail.setTitle("Report");

        this.createScreen(parent, "Report", 400, 500);
    }

    @FXML
    public void loadCategories(Event e) throws IOException {
        Node button = (Node) e.getSource();
        Node chart = button.getParent().getChildrenUnmodifiable().get(0);

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/report/report-categories.fxml")
        );
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        if (chart == this.expenseChart) {
            this.addPieChartData(this.expenseChart, this.pieChartDetail.getData());
        } else {
            this.addPieChartData(this.incomeChart, this.pieChartDetail.getData());
        }
        this.pieChartDetail.setTitle("Category");

        this.createScreen(parent, "Report", 400, 500);
    }

    @FXML
    public void loadDetail() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/report/report-detail.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        this.createScreen(parent, "Report Detail", 400, 500);
    }

    public XYChart.Series cloneBarChartData(BarChart chart) {
        ObservableList<XYChart.Data> oldData = ((XYChart.Series) chart.getData().get(0)).getData();
        XYChart.Series newSeries = new XYChart.Series();
        ObservableList<XYChart.Data> data = newSeries.getData();

        for (int i = 0; i < oldData.size(); i++) {
            XYChart.Data value = oldData.get(i);
            data.add(new XYChart.Data(value.getXValue(), value.getYValue()));
        }

        return newSeries;
    }

    public void addPieChartData(PieChart chart, ObservableList data) {
        for (PieChart.Data value: chart.getData()) {
            PieChart.Data category = new PieChart.Data(value.getName(), value.getPieValue());
            data.add(category);
        }
    }

    public void loadDetailData(ArrayList values) throws IOException {

    }
}
