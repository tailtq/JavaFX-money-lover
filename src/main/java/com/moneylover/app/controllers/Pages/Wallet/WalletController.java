package com.moneylover.app.controllers.Pages.Wallet;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Wallet.Entities.UserWallet;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.controllers.PageController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
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
    private IntegerProperty deletedWalletId = new SimpleIntegerProperty(0);

    private IntegerProperty updatedWalletId = new SimpleIntegerProperty(0);

    private com.moneylover.Modules.Wallet.Controllers.WalletController walletController; // Get Detail when update

    private CurrencyController currencyController;

    public WalletController(BooleanProperty changeWallet) throws SQLException, ClassNotFoundException {
        this.changeWallet = changeWallet;
        this.walletController = new com.moneylover.Modules.Wallet.Controllers.WalletController();
        this.currencyController = new CurrencyController(this.selectedCurrencyId);
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewWallets;

    @FXML
    private TextField textFieldTransactionName;

    @FXML
    private TextField textFieldTransactionAmount;

    @FXML
    private Button selectCurrency;

    private IntegerProperty selectedCurrencyId = new SimpleIntegerProperty(0);

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
                    return new WalletCell(currencyController.getCurrencies(), updatedWalletId, deletedWalletId);
                } catch (IOException | SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    private void handleWalletId() {
        this.updatedWalletId.addListener((observableValue, oldValue, newValue) -> {
            int i = 0;
            for (Wallet wallet: this.wallets) {
                if (wallet.getId() == newValue.intValue()) {
                    try {
                        this.wallets.set(i, this.walletController.getDetail(wallet.getId()));
                        this.loadHeaderWallets();
                    } catch (SQLException | NotFoundException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                i++;
            }
        });
        this.deletedWalletId.addListener((observableValue, oldValue, newValue) -> {
            int i = 0;
            for (Wallet wallet: this.wallets) {
                if (wallet.getId() == newValue.intValue()) {
                    this.wallets.remove(i);
                    this.loadHeaderWallets();
                    return;
                }
                i++;
            }
        });
    }

    @Override
    public void setWallets(ObservableList<Wallet> wallets) {
        super.setWallets(wallets);
        this.setListViewWallets();
    }

    @FXML
    private void listCurrencies() throws IOException {
        this.currencyController.loadCurrencies();
    }

    @FXML
    public void createWallet(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/wallet-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        this.selectedCurrencyId.set(0);
        this.currencyController.handleSelectedCurrencyId(this.selectCurrency);

        this.createScreen(parent, "Create Wallet", 500, 115);
    }

    @FXML
    private void storeWallet(Event event) throws NotFoundException {
        String name = this.textFieldTransactionName.getText().trim();
        String amountText = this.textFieldTransactionAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.trim());
        int currencyId = this.selectedCurrencyId.get();

        if (name.isEmpty() || currencyId == 0) {
            this.showErrorDialog("Please input all needed information");
            return;
        }
        try {
            Wallet wallet = this.walletController.create(new Wallet(currencyId, name, amount));
            ArrayList<UserWallet> userWallet = new ArrayList<>();
            userWallet.add(new UserWallet(this.user.getId(), wallet.getId()));
            this.walletController.attachUsers(userWallet);
            this.wallets.add(0, wallet);

            this.closeScene(event);
        } catch (SQLException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }
}
