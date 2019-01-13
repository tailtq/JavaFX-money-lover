package com.moneylover.app.controllers.Pages.Wallet;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Currency.Entities.Currency;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.controllers.Contracts.DialogInterface;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

class WalletCell extends ListCell<Wallet> implements DialogInterface {
    private com.moneylover.Modules.Wallet.Controllers.WalletController walletController;

    private CurrencyController currencyController;

    private Wallet wallet;

    private IntegerProperty updatedWalletId;

    private IntegerProperty deletedWalletId;

    private HBox walletCell;

    WalletCell(ObservableList<Currency> currencies, IntegerProperty updatedWalletId, IntegerProperty deletedWalletId) throws IOException, SQLException, ClassNotFoundException {
        this.walletController = new com.moneylover.Modules.Wallet.Controllers.WalletController();
        this.currencyController = new CurrencyController(this.selectedCurrencyId, currencies);
        this.updatedWalletId = updatedWalletId;
        this.deletedWalletId = deletedWalletId;

        FXMLLoader walletCellLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/wallet/wallet-cell.fxml"));
        walletCellLoader.setController(this);
        walletCell = walletCellLoader.load();
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewCurrencies;

    @FXML
    private Label labelWalletName;

    @FXML
    private Label labelWalletAmount;

    @FXML
    private TextField textFieldTransactionName;

    @FXML
    private TextField textFieldTransactionAmount;

    @FXML
    private Button selectCurrency;

    private IntegerProperty selectedCurrencyId = new SimpleIntegerProperty(0);

    protected void updateItem(Wallet item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            float amount = item.getInflow() - item.getOutflow();
            String amountText = Float.toString(amount);
            if (amount > 0) {
                this.labelWalletAmount.getStyleClass().add("success-color");
                amountText = "+" + amountText;
            } else if (amount < 0) {
                this.labelWalletAmount.getStyleClass().add("danger-color");
            }

            this.labelWalletName.setText(item.getName());
            this.labelWalletAmount.setText(amountText);
            setGraphic(walletCell);
        }
        this.wallet = item;
    }

    @FXML
    private void listCurrencies() throws IOException {
        this.currencyController.loadCurrencies();
    }

    @FXML
    private void editWallet() throws SQLException, IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/components/dialogs/wallet-edit.fxml")
        );
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        this.selectedCurrencyId.set(0);
        this.currencyController.handleSelectedCurrencyId(this.selectCurrency);

        this.selectedCurrencyId.set(this.wallet.getCurrencyId());
        this.textFieldTransactionName.setText(this.wallet.getName());
        this.textFieldTransactionAmount.setText(Float.toString(this.wallet.getInflow() - this.wallet.getOutflow()));

        this.createScreen(parent, "Edit Wallet", 500, 115);
    }

    @FXML
    private void updateWallet(Event event) {
        String name = this.textFieldTransactionName.getText().trim();
        String amountText = this.textFieldTransactionAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.trim());
        int currencyId = this.selectedCurrencyId.get();

        if (name.isEmpty() || currencyId == 0) {
            this.showErrorDialog("Please input all needed information");
            return;
        }
        try {
            this.walletController.update(new Wallet(currencyId, name, amount), this.wallet.getId());
            this.updatedWalletId.set(0);
            this.updatedWalletId.set(this.wallet.getId());

            this.closeScene(event);
        } catch (SQLException | NotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }

    @FXML
    private void deleteWallet(Event e) {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();

        if (buttonData == ButtonBar.ButtonData.YES) {
            try {
                int id = this.wallet.getId();
                this.walletController.delete(id);
                this.deletedWalletId.set(id);
            } catch (SQLException e1) {
                e1.printStackTrace();
                this.showErrorDialog("An error has occurred");
            }
        }
    }

    @FXML
    private void closeScene(Event e) {
        Node node = (Node) e.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
