package com.moneylover.app.controllers.Pages.Transaction;

import com.moneylover.Modules.Time.Entities.Day;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;

public class TransactionDate extends ListCell<Pair<Day, ObservableList<Transaction>>> {
    private VBox hBoxTransactionDate;

    TransactionDate() throws IOException {
        FXMLLoader transactionDateLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/pages/transaction/transaction-date.fxml")
        );
        transactionDateLoader.setController(this);
        this.hBoxTransactionDate = transactionDateLoader.load();
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewTransactions;

    @Override
    protected void updateItem(Pair<Day, ObservableList<Transaction>> item, boolean empty) {
        if (empty) {
            setGraphic(null);
        } else {
            this.listViewTransactions.setItems(item.getValue());
            this.listViewTransactions.setCellFactory(new Callback<ListView, ListCell>() {
                @Override
                public ListCell call(ListView param) {
                    try {
                        return new TransactionCell();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });
            this.listViewTransactions.prefHeightProperty().bind(Bindings.size(listViewTransactions.getItems()).multiply(50));
            setGraphic(this.hBoxTransactionDate);
        }
    }
}
