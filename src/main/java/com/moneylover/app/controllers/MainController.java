package com.moneylover.app.controllers;

import com.moneylover.app.controllers.Pages.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import com.moneylover.app.controllers.Contracts.LoaderInterface;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends BaseViewController implements Initializable {
    private BooleanProperty changeScene = new SimpleBooleanProperty(false);

    private VBox mainView;

    private LoaderInterface controller;

    public BooleanProperty getChangeScene() {
        return changeScene;
    }

    public void setChangeScene(boolean changeScene) {
        this.changeScene.setValue(changeScene);
    }

    public VBox getMainView() {
        return this.mainView;
    }

    public LoaderInterface getController() {
        return this.controller;
    }

    @FXML
    private void pressTransaction(Event e) throws IOException {
        this.initView(new TransactionController(), (Node) e.getSource());
    }

    @FXML
    private void pressReport(Event e) throws IOException {
        this.initView(new ReportController(), (Node) e.getSource());
    }

    @FXML
    private void pressBudget(Event e) throws IOException {
        this.initView(new BudgetController(), (Node) e.getSource());
    }

    @FXML
    private void pressWallet(Event e) throws IOException {
        this.initView(new WalletController(), (Node) e.getSource());
    }

    @FXML
    private void pressUser(Event e) throws IOException {
        this.initView(new UserController(), (Node) e.getSource());
    }

    private void initView(LoaderInterface controller, Node button) throws IOException {
        this.controller = controller;
        boolean notActive = this.activeButton(button);

        if (notActive) {
            this.mainView = this.controller.loadView();
            this.setChangeScene(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.controller = new TransactionController();
            this.mainView = this.controller.loadView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
