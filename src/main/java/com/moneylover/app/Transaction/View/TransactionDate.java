package com.moneylover.app.Transaction.View;

import com.moneylover.Modules.Time.Entities.CustomDate;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.SQLException;

public class TransactionDate extends ListCell<Pair<CustomDate, ObservableList<Transaction>>> {
    private VBox hBoxTransactionDate;

    private StringProperty handledTransactionId;

    private Pair<CustomDate, ObservableList<Transaction>> transactionDate;

    private ObservableList<Wallet> wallets;

    public TransactionDate(StringProperty handledTransactionId, ObservableList<Wallet> wallets) throws IOException {
        this.handledTransactionId = handledTransactionId;
        this.wallets = wallets;
        FXMLLoader transactionDateLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/pages/transaction/transaction-date.fxml")
        );
        transactionDateLoader.setController(this);
        this.hBoxTransactionDate = transactionDateLoader.load();
    }

    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewTransactions;

    @FXML
    private Label labelTransactionDayOfMonth;

    @FXML
    private Label labelTransactionDayOfWeek;

    @FXML
    private Label labelTransactionMonth;

    @FXML
    private Label labelTransactionAmount;

    @Override
    protected void updateItem(Pair<CustomDate, ObservableList<Transaction>> item, boolean empty) {
        if (empty) {
            setGraphic(null);
            return;
        }

        this.transactionDate = item;
        CustomDate customDate = item.getKey();
        labelTransactionDayOfMonth.setText(Integer.toString(customDate.getDayOfMonth()));
        labelTransactionDayOfWeek.setText(customDate.getDayOfWeek());
        labelTransactionMonth.setText(customDate.getMonth());
        labelTransactionAmount.setText(String.format("%.1f", this.calculateAmount(item.getValue())) + " " + customDate.getSymbol());
        setGraphic(this.hBoxTransactionDate);

        if (this.listViewTransactions.getItems().size() == 0) {
            this.listTransactions(item);
        }
    }

    private void listTransactions(Pair<CustomDate, ObservableList<Transaction>> item) {
        this.listViewTransactions.setItems(item.getValue());
        this.listViewTransactions.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                try {
                    TransactionCell transactionCell = new TransactionCell(handledTransactionId);
                    transactionCell.setWallets(wallets);

                    return transactionCell;
                } catch (IOException | SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        this.listViewTransactions.prefHeightProperty().bind(Bindings.size(listViewTransactions.getItems()).multiply(50));
    }

    private float calculateAmount(ObservableList<Transaction> transactions) {
        float amount = 0;

        for (Transaction transaction: transactions) {
            amount += transaction.getAmount();
        }

        return amount;
    }
}
