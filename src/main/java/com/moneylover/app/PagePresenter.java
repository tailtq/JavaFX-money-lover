package com.moneylover.app;

import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.Infrastructure.Contracts.LoaderInterface;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.sql.SQLException;

abstract public class PagePresenter extends BaseViewPresenter implements LoaderInterface {
    protected IntegerProperty changeWallet;

    protected ObservableList<Wallet> wallets;

    public void setChangeWallet(IntegerProperty changeWallet) {
        this.changeWallet = changeWallet;
    }

    /*========================== Draw ==========================*/
    @FXML
    protected MenuButton dropdownWallets;

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException {
        this.wallets = wallets;
        this.loadHeaderWallets();
    }

    public void setWalletsOnly(ObservableList<Wallet> wallets) {
        this.wallets = wallets;
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
        super.closeScene(e);
    }
}
