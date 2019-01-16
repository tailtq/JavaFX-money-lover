package com.moneylover.app.controllers;

import com.moneylover.Modules.User.Entities.User;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.controllers.Contracts.LoaderInterface;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.sql.SQLException;

abstract public class PageController extends BaseViewController implements LoaderInterface {
    protected BooleanProperty changeWallet;

    protected ObservableList<Wallet> wallets;

    protected User user;

    public void setUser(User user) {
        this.user = user;
    }

    /*========================== Draw ==========================*/
    @FXML
    protected MenuButton dropdownWallets;

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException {
        this.wallets = wallets;
        this.loadHeaderWallets();
    }

    protected void loadHeaderWallets() {
        ObservableList<MenuItem> items = this.dropdownWallets.getItems();
        items.clear();

        for (Wallet wallet: this.wallets) {
            MenuItem menuItem = new MenuItem(wallet.getName());
            menuItem.getStyleClass().add("header__wallet");
            items.add(menuItem);
        }
    }

    @FXML
    public void closeScene(Event e) {
        Node node = (Node) e.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
