package com.moneylover.app;

import com.moneylover.Infrastructure.Contracts.DialogInterface;
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
    protected IntegerProperty walletIndex;

    protected ObservableList<Wallet> wallets;

    public void setWalletIndex(IntegerProperty walletIndex) {
        this.walletIndex = walletIndex;
    }

    public Wallet getWallet() {
        return this.wallets.get(this.walletIndex.get());
    }

    protected int getWalletIndexId() {
        int i = 0, index = this.walletIndex.get();

        for (Wallet wallet: this.wallets) {
            if (index == i) {
                return wallet.getId();
            }

            i++;
        }

        return 0;
    }

    /*========================== Draw ==========================*/
    @FXML
    protected MenuButton dropdownWallets;

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException, InterruptedException {
        this.wallets = wallets;
        this.loadHeaderWallets();
    }

    protected void loadHeaderWallets() {
        if (this.wallets.size() == 0) {
            this.dropdownWallets.setText("No wallet is selected");
            this.dropdownWallets.getItems().clear();
            return;
        }

        this.dropdownWallets.setText(this.wallets.get(this.walletIndex.get()).getName());
        ObservableList<MenuItem> items = this.dropdownWallets.getItems();
        items.clear();
        int i = 0;

        for (Wallet wallet: this.wallets) {
            int j = i;
            MenuItem menuItem = new MenuItem(wallet.getName());
            menuItem.setOnAction(actionEvent -> {
                this.walletIndex.set(j);
                this.dropdownWallets.setText(wallet.getName());
            });
            menuItem.getStyleClass().add("header__wallet");
            items.add(menuItem);
            i++;
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
        DialogInterface.closeScene(e);
    }
}
