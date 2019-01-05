package com.moneylover.app;

import javafx.scene.*;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.collections.ObservableList;
import com.moneylover.app.controllers.MainController;

public class Main extends Application {
    private HBox layout = new HBox();

    private VBox content = new VBox();

    private FXMLLoader headerLoader;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/sidebar.fxml"));
        Parent sidebar = sidebarLoader.load();
        this.headerLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/header.fxml"));
        this.content.getChildren().add(this.headerLoader.load());
        this.content.getStylesheets().add(getClass().getResource("/assets/css/content.css").toExternalForm());

        this.layout.getChildren().addAll(sidebar, this.content);
        this.changeMainView(sidebarLoader.getController());

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(this.layout, 530, 500));
        primaryStage.setMinWidth(530);
        primaryStage.show();
    }

    private void changeMainView(MainController mainController) {
        mainController.getChangeScene().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                ObservableList<Node> nodes = this.content.getChildren();
                if (nodes.size() == 2) {
                    nodes.remove(1);
                }

                nodes.add(mainController.getMainView());
                this.headerLoader.setController(mainController.getController());
                // The difference between oldValue and newValue triggers an event
                mainController.setChangeScene(false);
            }
        });

        this.content.getChildren().add(mainController.getMainView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
