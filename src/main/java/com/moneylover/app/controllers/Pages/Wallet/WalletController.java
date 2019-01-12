package com.moneylover.app.controllers.Pages.Wallet;

import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.controllers.PageController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class WalletController extends PageController {
    private IntegerProperty handledWalletId = new SimpleIntegerProperty(0);

    private com.moneylover.Modules.Wallet.Controllers.WalletController walletController;

    public WalletController(BooleanProperty changeWallet) throws SQLException, ClassNotFoundException {
        this.changeWallet = changeWallet;
        this.walletController = new com.moneylover.Modules.Wallet.Controllers.WalletController();
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewWallets;

    @FXML
    private TextField textFieldTransactionName;

    @FXML
    private TextField textFieldTransactionAmount;

    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/wallet/wallet.fxml"));
        fxmlLoader.setController(this);
        VBox vBox = fxmlLoader.load();

        return vBox;
    }

    public void setListViewWallets() {
        this.handleWalletId();
        this.listViewWallets.setItems(this.wallets);
        this.listViewWallets.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell<Wallet> call(ListView param) {
                try {
                    return new WalletCell(handledWalletId);
                } catch (IOException | SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    private void handleWalletId() {
        this.handledWalletId.addListener((observableValue, oldValue, newValue) -> {
            int i = 0;
            for (Wallet wallet: this.wallets) {
                if (wallet.getId() == newValue.intValue()) {
                    this.wallets.remove(i);
                    return;
                }
                i++;
            }
        });
    }

    @Override
    public void setWallets(ArrayList<Wallet> wallets) {
        MenuItem menuItem;
        for (Wallet wallet: wallets) {
            menuItem = new MenuItem(wallet.getName());
            menuItem.getStyleClass().add("header__wallet");
            this.mDdWallets.getItems().add(menuItem);
        }

        this.wallets.setAll(wallets);
        this.setListViewWallets();
    }

    public void loadElements(ArrayList<Wallet> wallets) {
        this.wallets.addAll(wallets);
    }

    @FXML
    public void createWallet(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/wallet-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        this.createScreen(parent, "Create Wallet", 500, 115);
    }

    @FXML
    public void chooseCurrency() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/choose-currency.fxml"));
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();

        this.createScreen(parent, "Choose Currency", 400, 300);
    }
}
