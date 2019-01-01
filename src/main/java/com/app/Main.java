package main.java.com.app;

import javafx.scene.*;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.collections.ObservableList;
import main.java.com.app.controllers.MainController;

public class Main extends Application {
    private HBox layout = new HBox();

    private VBox content;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("../../../resources/views/components/sidebar.fxml"));
        Parent sidebar = sidebarLoader.load();
        FXMLLoader contentLoader = new FXMLLoader(getClass().getResource("../../../resources/views/components/header.fxml"));
        this.content = contentLoader.load();

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
