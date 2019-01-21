package com.moneylover.app.Budget;

import com.moneylover.Infrastructure.Contracts.UseCategoryInterface;
import com.moneylover.app.PagePresenter;
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
import java.util.ArrayList;

public class BudgetPresenter extends PagePresenter implements UseCategoryInterface {
    @FXML
    private AreaChart areaChartDetail;

    @FXML
    private TabPane budgetsTabPane;

    @FXML
    private VBox runningTab;

    @FXML
    private VBox finishedTab;

    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/budget/budget.fxml"));
        fxmlLoader.setController(this);
        VBox vBox = fxmlLoader.load();

        return vBox;
    }

    @FXML
    private void changeTab(Event e) {
        this.activeTab(e, this.budgetsTabPane);
    }

    @FXML
    private void chooseCategory(Event e) throws IOException {
        this.showCategoryDialog(e);
    }

    @FXML
    private void showBudget(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/budget/budget-show.fxml"));
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
    private void createBudget(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/budget/budget-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        this.createScreen(parent, "Create Budget", 500, 170);
    }

    @FXML
    private void editBudget(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/budget/budget-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        this.createScreen(parent, "Edit Budget", 500, 170);
    }

    @FXML
    private void deleteBudget(Event e) {
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
