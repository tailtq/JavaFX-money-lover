package com.moneylover.app.Wallet;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Wallet.Controllers.WalletController;
import com.moneylover.Modules.Wallet.Entities.UserWallet;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.Currency.CurrencyPresenter;
import com.moneylover.app.PagePresenter;
import com.moneylover.app.User.UserPresenter;
import com.moneylover.app.Wallet.View.WalletCell;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WalletPresenter extends PagePresenter implements Initializable {
    private StringProperty handledWalletId = new SimpleStringProperty();

    private com.moneylover.Modules.Wallet.Controllers.WalletController walletController; // Get Detail when update

    public void loadPresenter() throws SQLException, ClassNotFoundException {
        this.walletController = new com.moneylover.Modules.Wallet.Controllers.WalletController();
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewWallets;

    @FXML
    private TextField textFieldTransactionName;

    @FXML
    private TextField textFieldWalletAmount;

    @FXML
    private Button selectCurrency;

    private IntegerProperty selectedCurrencyId = new SimpleIntegerProperty(0);

    public void setListViewWallets() {
        this.handleWalletId();

        if (this.wallets.size() == 0) {
            this.listViewWallets.setPlaceholder(new Label("No Wallet In List"));
        }

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
            if (newValue == null) {
                return;
            }

            int i = 0;
            int id = Integer.parseInt(newValue.substring(7));

            for (Wallet wallet: this.wallets) {
                if (wallet.getId() == id) {
                    if (newValue.contains("DELETE-")) {
                        break;
                    } else {
                        try {
                            this.wallets.set(i, this.walletController.getDetail(id));
                        } catch (SQLException | NotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

                i++;
            }

            if (newValue.contains("DELETE-")) {
                int walletIndex = this.walletIndex.get();

                if (walletIndex == i) {
                    this.walletIndex.set(0);
                } else if (walletIndex > i) {
                    this.walletIndex.set(walletIndex - 1);
                }

                this.wallets.remove(i);
            }

            this.loadHeaderWallets();
        });
    }

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException {
        super.setWallets(wallets);
        this.setListViewWallets();
    }

    @FXML
    private void listCurrencies() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/choose-currency/choose-currency.fxml")
        );
        Parent parent = fxmlLoader.load();
        CurrencyPresenter currencyPresenter = fxmlLoader.getController();
        currencyPresenter.setSelectedCurrencyId(selectedCurrencyId);
        currencyPresenter.handleSelectedCurrencyId(selectCurrency);

        this.createScreen(parent, "Choose Currency", 400, 300);
    }

    @FXML
    public void createWallet() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/wallet/wallet-create.fxml")
        );
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        this.createScreen(parent, "Create Wallet", 500, 115);
    }

    @FXML
    private void storeWallet(Event event) throws NotFoundException, SQLException, ClassNotFoundException {
        this.walletController = new WalletController();

        String name = this.textFieldTransactionName.getText().trim();
        String amountText = this.textFieldWalletAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.trim());
        int currencyId = this.selectedCurrencyId.get();

        if (name.isEmpty() || currencyId == 0) {
            this.showErrorDialog("Please input all needed information");
            return;
        }
        try {
            Wallet wallet = this.walletController.create(new Wallet(currencyId, name, amount));
            ArrayList<UserWallet> userWallet = new ArrayList<>();

            userWallet.add(new UserWallet(UserPresenter.getUser().getId(), wallet.getId()));
            this.walletController.attachUsers(userWallet);
            this.wallets.add(0, wallet);

            this.closeScene(event);
        } catch (SQLException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
