package com.moneylover.app.controllers;

import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.controllers.Contracts.LoaderInterface;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;

import java.util.ArrayList;

abstract public class PageController extends BaseViewController implements LoaderInterface {
    @FXML
    protected MenuButton wallets;

    @Override
    public void setWallets(ArrayList<Wallet> wallets) {
        System.out.println(this.wallets.getChildrenUnmodifiable());
    }
}
