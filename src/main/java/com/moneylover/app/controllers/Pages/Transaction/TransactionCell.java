package com.moneylover.app.controllers.Pages.Transaction;

import com.moneylover.Modules.Transaction.Entities.Transaction;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TransactionCell extends ListCell<Transaction> {
    private HBox transactionCell;

    TransactionCell() throws IOException {
        FXMLLoader transactionCellLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/transaction/transaction-cell.fxml"));
        transactionCellLoader.setController(this);
        transactionCell = transactionCellLoader.load();
    }

    /*========================== Draw ==========================*/
    @Override
    protected void updateItem(Transaction item, boolean empty) {
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(this.transactionCell);
        }
    }
}
