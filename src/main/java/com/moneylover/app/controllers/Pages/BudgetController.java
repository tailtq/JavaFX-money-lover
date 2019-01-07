package com.moneylover.app.controllers.Pages;

import com.moneylover.app.controllers.BaseViewController;
import com.moneylover.app.controllers.Contracts.UseCategoryInterface;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import com.moneylover.app.controllers.Contracts.LoaderInterface;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

public class BudgetController extends BaseViewController implements LoaderInterface, UseCategoryInterface {
    @FXML
    private AreaChart areaChartDetail;

    @FXML
    private TabPane budgetsTabPane;

    @FXML
    private VBox runningTab;

    @FXML
    private VBox finishedTab;

    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/budget.fxml"));
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/budget-show.fxml"));
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();

        ArrayList<Pair<String, Double>> values = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            values.add(new Pair<>(Integer.toString(i), (double) i * 100));
        }

        this.showAreaChart(values);

        Stage stage = new Stage();
        stage.setScene(new Scene(parent, 500, 700));
        stage.setTitle("Create Budget");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/report-detail.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        Stage stage = new Stage();

        stage.setScene(new Scene(parent, 500, 700));
        stage.setTitle("Budget Detail");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void createBudget(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/budget-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(parent, 500, 170));
        stage.setTitle("Create Budget");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void editBudget(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/budget-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(parent, 500, 170));
        stage.setTitle("Edit Budget");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void deleteBudget(Event e) {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            // Delete Budget
            System.out.println("Yes");
        }
    }
}
