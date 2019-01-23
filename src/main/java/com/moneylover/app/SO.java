package com.moneylover.app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.logging.Logger;

public class SO extends Application {
    private NumberAxis yAxis;
    private CategoryAxis xAxis;
    private BarChart<String, Number> barChart;

    private Parent createContent() {
        xAxis = new CategoryAxis();
        CategoryAxis xAxis1 = new CategoryAxis();
        yAxis = new NumberAxis();
        NumberAxis yAxis1 = new NumberAxis();
        barChart = new BarChart<>(xAxis, yAxis);

        Button initData = new Button("init");
        initData.setOnAction(e -> {
            xAxis.setLabel("Numer indeksu");
            yAxis.setLabel("Ilo punktw");
            XYChart.Series<String, Number> series1 = new XYChart.Series<>();
            series1.getData().add(new XYChart.Data<String, Number>("Tom", 10));
            series1.getData().add(new XYChart.Data<String, Number>("Andrew", 7));
            series1.getData().add(new XYChart.Data<String, Number>("Patrick", 5));

            // hack-around:
            xAxis.setCategories(FXCollections.observableArrayList("Tom", "Andrew", "Patrick"));
            barChart.getData().addAll(series1);

            initData.setDisable(true);

        });
        BorderPane pane = new BorderPane(barChart);
        pane.setBottom(initData);
        return pane;

    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        stage.setTitle("G");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}