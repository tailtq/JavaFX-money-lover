package com.moneylover.app.controllers.Pages;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import com.moneylover.app.controllers.Contracts.LoaderInterface;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.io.IOException;
import java.util.ArrayList;

public class ReportController implements LoaderInterface {
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
    private GridPane transactionPane;

    @FXML
    private Text title;

    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/report/report.fxml"));
        fxmlLoader.setController(this);
        VBox content = fxmlLoader.load();

        // TODO: Calculate data
        ArrayList<Pair<String, Integer>> values = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            values.add(new Pair<>("Hello " + i, i * 100));
        }
        this.loadBarChartData(values);
        this.loadPieChartData(this.expenseChart, values);
        this.loadPieChartData(this.incomeChart, values);

        return content;
    }

    public void loadBarChartData(ArrayList<Pair<String, Integer>> values) {
        XYChart.Series series = new XYChart.Series();
        ObservableList data = series.getData();

        for (Pair<String, Integer> value: values) {
            data.add(new XYChart.Data(value.getKey(), value.getValue()));
        }

        this.barChart.getData().add(series);
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/report-months.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        XYChart.Series clonalData = this.cloneBarChartData(this.barChart);
        this.barChartDetail.getData().add(clonalData);
        this.barChartDetail.setTitle("Report");

        Stage stage = new Stage();
        stage.setScene(new Scene(parent, 500, 700));
        stage.setTitle("Report");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    public void loadCategories(Event e) throws IOException {
        Node button = (Node) e.getSource();
        Node chart = button.getParent().getChildrenUnmodifiable().get(0);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/report-categories.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        if (chart == this.expenseChart) {
            this.addPieChartData(this.expenseChart, this.pieChartDetail.getData());
        } else {
            this.addPieChartData(this.incomeChart, this.pieChartDetail.getData());
        }
        this.pieChartDetail.setTitle("Category");

        Stage stage = new Stage();
        stage.setScene(new Scene(parent, 500, 700));
        stage.setTitle("Report");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    public void loadDetail() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/report-detail.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        Stage stage = new Stage();

        stage.setScene(new Scene(parent, 500, 700));
        stage.setTitle("Report Detail");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
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
