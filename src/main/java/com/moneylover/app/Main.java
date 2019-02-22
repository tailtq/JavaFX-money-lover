package com.moneylover.app;

import com.moneylover.app.User.AuthenticationPresenter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

    private boolean firstLogin = true;

    private StringProperty changeMainScene = new SimpleStringProperty("signin");

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.authenticationController = new AuthenticationPresenter(changeMainScene);
        this.listenSceneChanging();
    }

    private void listenSceneChanging() throws IOException {
        this.changeMainScene.addListener((observableValue, oldValue, newValue) -> {
            try {
                this.layout.getChildren().clear();

                if (newValue.equals("signin") || newValue.equals("signup")) {
                    this.primaryStage.setMinWidth(320);
                    this.primaryStage.setMinHeight(320);

                    if (newValue.equals("signup")) {
                        this.primaryStage.setScene(this.authenticationController.loadSignUpForm());
                    } else {
                        this.primaryStage.setScene(this.authenticationController.loadSignInForm());
                    }
                } else {
                    this.loadMainScene();
                    firstLogin = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.primaryStage.setScene(this.authenticationController.loadSignInForm());
        this.primaryStage.setMinWidth(320);
        this.primaryStage.show();
    }

    private void loadMainScene() throws IOException {
        if (!firstLogin) {
            this.layout = new HBox();
        }

        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/sidebar.fxml"));
        Parent sidebar = sidebarLoader.load();

        this.layout.getChildren().add(sidebar);
        this.changeMainView(sidebarLoader.getController());
        this.primaryStage.setTitle("MoneyLover");
        this.primaryStage.setScene(new Scene(this.layout, 680, 550));
        this.primaryStage.setMinWidth(680);
        this.primaryStage.setMinHeight(550);
        this.primaryStage.show();
    }

    private void changeMainView(MainPresenter mainPresenter) {
        mainPresenter.getChangeScene().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                if (this.layout.getChildren().size() == 2) {
                    this.layout.getChildren().set(1, mainPresenter.getMainView());
                } else {
                    this.layout.getChildren().add(mainPresenter.getMainView());
                }

                mainPresenter.setChangeScene(false);
            }
        });
        mainPresenter.setChangeScene(true);
        mainPresenter.setChangeMainScene(this.changeMainScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
