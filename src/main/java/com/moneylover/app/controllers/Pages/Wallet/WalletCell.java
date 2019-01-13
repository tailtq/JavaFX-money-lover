package com.moneylover.app.controllers.Pages.Wallet;

import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.controllers.Contracts.DialogInterface;
import javafx.beans.property.IntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;

class WalletCell extends ListCell<Wallet> implements DialogInterface {
    private com.moneylover.Modules.Wallet.Controllers.WalletController walletController;

    private Wallet wallet;

    private IntegerProperty deletedWalletId;

    private HBox walletCell;

    public WalletCell(IntegerProperty deletedWalletId) throws IOException, SQLException, ClassNotFoundException {
        this.walletController = new com.moneylover.Modules.Wallet.Controllers.WalletController();
        this.deletedWalletId = deletedWalletId;

        FXMLLoader walletCellLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/wallet/wallet-cell.fxml"));
        walletCellLoader.setController(this);
        walletCell = walletCellLoader.load();
    }

    /*========================== Draw ==========================*/
    @FXML
    private Label labelWalletName;

    @FXML
    private Label labelWalletAmount;

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
            } else {
                this.labelWalletAmount.getStyleClass().removeAll("success-color, danger-color");
            }

            this.labelWalletName.setText(item.getName());
            this.labelWalletAmount.setText(amountText);
            setGraphic(walletCell);
        }
        this.wallet = item;
    }

    @FXML
    public void editWallet(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/wallet-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();
//        this.deletedWalletId.set(id);

//            this.createScreen(parent, "Edit Wallet", 500, 115);
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
}
