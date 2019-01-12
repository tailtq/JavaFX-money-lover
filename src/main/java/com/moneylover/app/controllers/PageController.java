package com.moneylover.app.controllers;

import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.controllers.Contracts.LoaderInterface;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;

abstract public class PageController extends BaseViewController implements LoaderInterface {
    protected BooleanProperty changeWallet;

    protected ObservableList<Wallet> wallets = FXCollections.observableArrayList();

    @FXML
    protected MenuButton mDdWallets;

    @Override
    public void setWallets(ArrayList<Wallet> wallets) {
        MenuItem menuItem;
        for (Wallet wallet: wallets) {
            menuItem = new MenuItem(wallet.getName());
            menuItem.getStyleClass().add("header__wallet");
            this.mDdWallets.getItems().add(menuItem);
        }

        this.wallets.addAll(wallets);
    }
}
