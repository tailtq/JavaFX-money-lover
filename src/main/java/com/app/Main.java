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

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../../resources/views/components/sidebar.fxml"));
        Parent sidebar = fxmlLoader.load();

        this.layout.getChildren().add(sidebar);
        this.changeMainView(fxmlLoader.getController());

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(this.layout, 530, 500));
        primaryStage.setMinWidth(530);
        primaryStage.show();
    }

    private void changeMainView(MainController mainController) {
        mainController.getChangeScene().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                ObservableList<Node> nodes = this.layout.getChildren();
                if (nodes.size() == 2) {
                    nodes.remove(1);
                }

                nodes.add(mainController.getMainView());
                // The difference between oldValue and newValue triggers an event
                mainController.setChangeScene(false);
            }
        });

        this.layout.getChildren().add(mainController.getMainView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
