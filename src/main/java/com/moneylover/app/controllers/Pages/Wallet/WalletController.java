package com.moneylover.app.controllers.Pages.Wallet;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Currency.Controllers.CurrencyController;
import com.moneylover.Modules.Currency.Entities.Currency;
import com.moneylover.Modules.Wallet.Entities.UserWallet;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.controllers.PageController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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

    private ObservableList<Currency> currencies = FXCollections.observableArrayList();

    public WalletController(BooleanProperty changeWallet) throws SQLException, ClassNotFoundException {
        this.changeWallet = changeWallet;
        this.walletController = new com.moneylover.Modules.Wallet.Controllers.WalletController();
        this.currencyController = new CurrencyController();
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewWallets;

    @FXML
    private ListView listViewCurrencies;

    @FXML
    private TextField textFieldTransactionName;

    @FXML
    private TextField textFieldTransactionAmount;

    @FXML
    private Button selectCurrency;

    private String currencyClassName;

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
                    return new WalletCell(deletedWalletId);
                } catch (IOException | SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    private void handleWalletId() {
        this.deletedWalletId.addListener((observableValue, oldValue, newValue) -> {
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
        this.wallets.setAll(wallets);
        this.setListViewWallets();

        MenuItem menuItem;
        for (Wallet wallet: wallets) {
            menuItem = new MenuItem(wallet.getName());
            menuItem.getStyleClass().add("header__wallet");
            this.dropdownWallets.getItems().add(menuItem);
        }
    }

    @FXML
    public void createWallet(Event e) throws IOException, SQLException {
        if (this.currencies.isEmpty()) {
            this.currencies.setAll(this.currencyController.list());
        }
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/wallet-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        this.createScreen(parent, "Create Wallet", 500, 115);
    }

    @FXML
    private void listCurrencies() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/choose-currency/choose-currency.fxml")
        );
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();

        this.handleSelectedCurrencyId();
        this.listViewCurrencies.setItems(this.currencies);
        this.listViewCurrencies.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell<Currency> call(ListView param) {
                try {
                    return new CurrencyCell(selectedCurrencyId);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        this.createScreen(parent, "Choose Currency", 400, 300);
    }

    private void handleSelectedCurrencyId() {
        this.selectedCurrencyId.addListener((observableValue, oldValue, newValue) -> {
            for (Currency currency: this.currencies) {
                if (currency.getId() == newValue.intValue()) {
                    ObservableList<String> classes = this.selectCurrency.getStyleClass();
                    if (this.currencyClassName != null) {
                        classes.remove(currencyClassName);
                    } else {
                        classes.add("select-currency__stylesheet");
                        this.selectCurrency.setAlignment(Pos.CENTER_LEFT);
                    }
                    this.currencyClassName = "currency__" + currency.getCode().toLowerCase();
                    this.selectCurrency.setText(currency.getName());
                    this.selectCurrency.getStyleClass().add(currencyClassName);
                    return;
                }
            }
        });
    }

    @FXML
    private void changeAmount(Event e) {
//        String amount = this.textFieldTransactionAmount.getText();
//        NumberFormat nf = NumberFormat.getNumberInstance();
//        System.out.println(nf.format(new BigDecimal(amount)));
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
