package com.moneylover.app.controllers;

import com.moneylover.Modules.User.Entities.User;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.controllers.Pages.*;
import com.moneylover.app.controllers.Pages.Wallet.WalletController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import com.moneylover.app.controllers.Contracts.LoaderInterface;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController extends BaseViewController implements Initializable {
    private User user;

    private ObservableList<Wallet> wallets = FXCollections.observableArrayList();

    private LoaderInterface controller;

    private com.moneylover.Modules.Wallet.Controllers.WalletController walletController;

    private BooleanProperty changeScene = new SimpleBooleanProperty(false);

    private BooleanProperty changeWallet = new SimpleBooleanProperty(false);

    private VBox mainView;

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

    public void setUser(User user) throws SQLException, IOException, ClassNotFoundException {
        this.user = user;
        this.controller.setUser(this.user);
        this.setWallets();
    }

    public void setWallets() throws IOException, SQLException, ClassNotFoundException {
        if (this.wallets.isEmpty()) {
            this.walletController = new com.moneylover.Modules.Wallet.Controllers.WalletController();
            this.wallets.addAll(walletController.listByUser(this.user.getId()));
        }
        this.controller.setWallets(this.wallets);
        this.changeWallet.addListener((observableValue, aBoolean, t1) -> {
            try {
                ObservableList<Wallet> updatedWallets = FXCollections.observableArrayList(walletController.listByUser(this.user.getId()));
                this.controller.setWallets(updatedWallets);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void pressTransaction(Event e) throws IOException, SQLException, ClassNotFoundException {
        this.initView(new TransactionController(), (Node) e.getSource());
    }

    @FXML
    private void pressReport(Event e) throws IOException, SQLException, ClassNotFoundException {
        this.initView(new ReportController(), (Node) e.getSource());
    }

    @FXML
    private void pressBudget(Event e) throws IOException, SQLException, ClassNotFoundException {
        this.initView(new BudgetController(), (Node) e.getSource());
    }

    @FXML
    private void pressWallet(Event e) throws IOException, SQLException, ClassNotFoundException {
        this.initView(new WalletController(this.changeWallet), (Node) e.getSource());
    }

    @FXML
    private void pressUser(Event e) throws IOException, SQLException, ClassNotFoundException {
        this.initView(new UserController(), (Node) e.getSource());
    }

    private void initView(LoaderInterface controller, Node button) throws IOException, SQLException, ClassNotFoundException {
        this.controller = controller;
        boolean notActive = this.activeButton(button);

        if (notActive) {
            this.mainView = this.controller.loadView();
            this.setWallets();
            this.setChangeScene(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.controller = new WalletController(this.changeScene);
            this.mainView = this.controller.loadView();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
