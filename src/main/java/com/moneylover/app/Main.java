package com.moneylover.app;

import com.moneylover.app.User.AuthenticationPresenter;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.collections.ObservableList;

import java.io.IOException;

public class Main extends Application {
    private Stage primaryStage;

    private AuthenticationPresenter authenticationController;

    private HBox layout = new HBox();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.authenticationController = new AuthenticationPresenter();
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
                    this.loadMainScene();
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        primaryStage.setScene(new Scene(this.layout, 680, 550));
        primaryStage.setMinWidth(680);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }

    private void changeMainView(MainPresenter mainPresenter) {
        mainPresenter.getChangeScene().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                ObservableList<Node> nodes = this.layout.getChildren();
                if (nodes.size() == 2) {
                    nodes.remove(1);
                }

                nodes.add(mainPresenter.getMainView());
                // The difference between oldValue and newValue triggers an event
                mainPresenter.setChangeScene(false);
            }
        });
        this.layout.getChildren().add(mainPresenter.getMainView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
