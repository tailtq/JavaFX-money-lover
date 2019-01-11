package com.moneylover.app;

import com.moneylover.Modules.User.Entities.User;
import com.moneylover.app.controllers.Pages.AuthenticationController;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.collections.ObservableList;
import com.moneylover.app.controllers.MainController;

import java.io.IOException;

public class Main extends Application {
    private Stage primaryStage;

    private AuthenticationController authenticationController;

    private HBox layout = new HBox();

    private User user;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.authenticationController = new AuthenticationController();
        this.listenSceneChanging();
    }

    private void listenSceneChanging() throws IOException {
        this.authenticationController.getChangeScene().addListener((observableValue, oldValue, newValue) -> {
            try {
                if (newValue.equals("signin") || newValue.equals("signup")) {
                    if (newValue.equals("signup")) {
                        this.primaryStage.setScene(this.authenticationController.loadSignUpForm());
                    } else {
                        this.primaryStage.setScene(this.authenticationController.loadSignInForm());
                    }
                    this.primaryStage.setMinWidth(270);
                } else {
                    this.user = this.authenticationController.getUser();
                    this.loadMainScene();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        this.primaryStage.setScene(this.authenticationController.loadView());
        this.primaryStage.setMinWidth(270);
        this.primaryStage.show();
    }

    private void loadMainScene() throws IOException {
        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/sidebar.fxml"));
        Parent sidebar = sidebarLoader.load();

        this.layout.getChildren().add(sidebar);
        this.changeMainView(sidebarLoader.getController());

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(this.layout, 530, 500));
        primaryStage.setMinWidth(530);
        primaryStage.setMinHeight(500);
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
