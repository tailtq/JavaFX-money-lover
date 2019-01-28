package com.moneylover.app;

import com.moneylover.Infrastructure.Contracts.ParserInterface;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.Infrastructure.Contracts.LoaderInterface;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.sql.SQLException;

abstract public class PagePresenter extends BaseViewPresenter implements LoaderInterface, ParserInterface {
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

    public static void loadStaticWallets(MenuButton selectWallet, IntegerProperty walletId, ObservableList<Wallet> wallets) {
        selectWallet.getItems().clear();
        int walletIdInt = walletId.get();

        for (Wallet wallet: wallets) {
            if (wallet.getId() == walletIdInt && walletIdInt != 0) {
                selectWallet.setText(wallet.getName());
                selectWallet.getStyleClass().add("header__wallet");
            }

            MenuItem item = new MenuItem();
            item.setText(wallet.getName());
            item.getStyleClass().add("header__wallet");
            item.setOnAction(e -> {
                MenuItem menuItem = (MenuItem) e.getSource();
                selectWallet.setText(menuItem.getText());
                selectWallet.getStyleClass().add("header__wallet");
                walletId.set(wallet.getId());
            });
            selectWallet.getItems().add(item);
        }
    }

    @FXML
    public void closeScene(Event e) {
        super.closeScene(e);
    }
}
