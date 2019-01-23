package com.moneylover.app.Budget;

import com.moneylover.Infrastructure.Helpers.DateHelper;
import com.moneylover.Modules.Budget.Controllers.BudgetController;
import com.moneylover.Modules.Budget.Entities.Budget;
import com.moneylover.Modules.Time.Entities.CustomDateRange;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.Category.CategoryPresenter;
import com.moneylover.app.PagePresenter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class BudgetPresenter extends PagePresenter {
    private CategoryPresenter categoryPresenter;

    private BudgetController budgetController;

    private ObservableList<Budget> onGoingBudgets = FXCollections.observableArrayList();

    private ObservableList<Budget> finishingBudgets = FXCollections.observableArrayList();

    public BudgetPresenter() throws SQLException, ClassNotFoundException {
        this.categoryPresenter = new CategoryPresenter(this.selectedType, this.selectedCategory, this.selectedSubCategory);
        this.budgetController = new BudgetController();
    }

    private static void _sortFinishingBudgets(
            ObservableList<Budget> onGoingBudgets,
            ObservableList<Budget> finishedBudgets,
            ArrayList<Budget> budgets
    ) {
        onGoingBudgets.clear();
        finishedBudgets.clear();
        LocalDate now = LocalDate.now();

        for (Budget budget: budgets) {
            LocalDate endDate = LocalDate.parse(budget.getStartedAt().toString());

            if (DateHelper.isLaterThan(now, endDate)) {
                onGoingBudgets.add(budget);
            } else {
                finishedBudgets.add(budget);
            }
        }
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewBudgets;

    @FXML
    private AreaChart areaChartDetail;

    @FXML
    private TabPane budgetsTabPane;

    @FXML
    private VBox runningTab;

    @FXML
    private VBox finishedTab;

    private IntegerProperty selectedType = new SimpleIntegerProperty(0);

    private IntegerProperty selectedCategory = new SimpleIntegerProperty(0);

    private IntegerProperty selectedSubCategory = new SimpleIntegerProperty(0);

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException {
        super.setWallets(wallets);
        Wallet wallet = wallets.get(0);
        ArrayList<Budget> budgets = this.budgetController.list(wallet.getId());
        BudgetPresenter._sortFinishingBudgets(onGoingBudgets, finishingBudgets, budgets);
    }

    @FXML
    private void changeTab(Event e) {
        this.activeTab(e, this.budgetsTabPane);
    }

    @FXML
    private void chooseCategory() throws IOException {
        this.categoryPresenter.showCategoryDialog();
    }

    @FXML
    private void showBudget() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/budget/budget-show.fxml")
        );
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();
        ArrayList<Pair<String, Double>> values = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            values.add(new Pair<>(Integer.toString(i), (double) i * 100));
        }

        this.showAreaChart(values);
        this.createScreen(parent, "Budget Detail", 400, 500);
    }

    @FXML
    private void showAreaChart(ArrayList<Pair<String, Double>> values) {
        XYChart.Series series = new XYChart.Series();
        ObservableList data = series.getData();

        for (Pair<String, Double> value: values) {
            data.add(new XYChart.Data(value.getKey(), value.getValue()));
        }

        this.areaChartDetail.getData().add(series);
    }

    @FXML
    private void loadBudgetTransactions() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/report/report-detail.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        this.createScreen(parent, "Budget Detail", 400, 500);
    }

    @FXML
    private void createBudget() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/budget/budget-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        this.createScreen(parent, "Create Budget", 500, 170);
    }

    @FXML
    private void editBudget() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/budget/budget-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        this.createScreen(parent, "Edit Budget", 500, 170);
    }

    @FXML
    private void deleteBudget() {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            // Delete Budget
            System.out.println("Yes");
        }
    }

    @Override
    public void loadPresenter() {

    }
}
