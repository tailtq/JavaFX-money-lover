package com.moneylover.app.Wallet.View;

import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.Infrastructure.Contracts.DialogInterface;
import com.moneylover.app.Currency.CurrencyPresenter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;

public class WalletCell extends ListCell<Wallet> implements DialogInterface {
    private StringProperty handledWalletId;

    private com.moneylover.Modules.Wallet.Controllers.WalletController walletController;

    private CurrencyPresenter currencyPresenter;

    private Wallet wallet;

    public WalletCell(StringProperty handledWalletId) throws IOException, SQLException, ClassNotFoundException {
        this.currencyPresenter = new CurrencyPresenter(this.selectedCurrencyId);
        this.walletController = new com.moneylover.Modules.Wallet.Controllers.WalletController();
        this.handledWalletId = handledWalletId;
        this.loadCell();
    }

    private void loadCell() throws IOException {
        FXMLLoader walletCellLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/wallet/wallet-cell.fxml"));
        walletCellLoader.setController(this);
        this.walletCell = walletCellLoader.load();
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    /*========================== Draw ==========================*/
    private HBox walletCell;

    @FXML
    private Label labelWalletName;

    @FXML
    private Label labelWalletAmount;

    @FXML
    private TextField textFieldTransactionName;

    @FXML
    private TextField textFieldWalletAmount;

    @FXML
    private Button selectCurrency;

    private IntegerProperty selectedCurrencyId = new SimpleIntegerProperty(0);

    protected void updateItem(Wallet item, boolean empty) {
        super.updateItem(item, empty);
        this.wallet = item;

        if (empty) {
            setGraphic(null);
            return;
        }

        float amount = item.getInflow() - item.getOutflow();
        String amountText = String.format("%.1f", amount) + " " + item.getMoneySymbol();

        if (amount > 0) {
            amountText = "+" + amountText;
            this.labelWalletAmount.getStyleClass().add("success-color");
        } else if (amount < 0) {
            this.labelWalletAmount.getStyleClass().add("danger-color");
        }

        this.labelWalletName.setText(item.getName());
        this.labelWalletAmount.setText(amountText);
        setGraphic(this.walletCell);
    }

    @FXML
    private void listCurrencies() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/choose-currency/choose-currency.fxml")
        );
        Parent parent = fxmlLoader.load();
        CurrencyPresenter currencyPresenter = fxmlLoader.getController();
        currencyPresenter.setSelectedCurrencyId(this.selectedCurrencyId);

        this.createScreen(parent, "Choose Currency", 400, 300);
    }

    @FXML
    private void editWallet() throws SQLException, IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/wallet/wallet-edit.fxml")
        );
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        // TODO: set data for new instance
        currencyPresenter.setSelectedCurrencyId(this.selectedCurrencyId);
        currencyPresenter.handleSelectedCurrencyId(this.selectCurrency);
        this.selectedCurrencyId.set(this.wallet.getCurrencyId());
        this.textFieldTransactionName.setText(this.wallet.getName());
        this.textFieldWalletAmount.setText(String.format("%.1f", this.wallet.getInflow() - this.wallet.getOutflow()));

        this.createScreen(parent, "Edit Wallet", 500, 115);
    }

    @FXML
    private void updateWallet(Event event) {
        String name = this.textFieldTransactionName.getText().trim();
        String amountText = this.textFieldWalletAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.trim());
        int currencyId = this.selectedCurrencyId.get();

        if (name.isEmpty() || currencyId == 0) {
            this.showErrorDialog("Please input all needed information");
            return;
        }
        try {
            this.walletController.update(new Wallet(currencyId, name, amount), this.wallet.getId());
            this.handledWalletId.set(null);
            this.handledWalletId.set("UPDATE-" + this.wallet.getId());

            this.closeScene(event);
        } catch (SQLException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }

    @FXML
    private void deleteWallet() {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();

        if (buttonData == ButtonBar.ButtonData.YES) {
            try {
                int id = this.wallet.getId();
                this.walletController.delete(id);
                this.handledWalletId.set("DELETE-" + id);
            } catch (SQLException e1) {
                e1.printStackTrace();
                this.showErrorDialog("An error has occurred");
            }
        }
    }
}
