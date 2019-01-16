package com.moneylover.app.controllers.Pages.Transaction;

import com.moneylover.Modules.Time.Entities.Day;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import javafx.beans.binding.Bindings;
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

    @FXML
    private Label labelTransactionDayOfMonth;

    @FXML
    private Label labelTransactionDayOfWeek;

    @FXML
    private Label labelTransactionMonth;

    @FXML
    private Label labelTransactionAmount;

    @Override
    protected void updateItem(Pair<Day, ObservableList<Transaction>> item, boolean empty) {
        if (empty) {
            setGraphic(null);
        } else {
            Day day = item.getKey();
            labelTransactionDayOfMonth.setText(Integer.toString(day.getDayOfMonth()));
            labelTransactionDayOfWeek.setText(day.getDayOfWeek());
            labelTransactionMonth.setText(day.getMonth());
            labelTransactionAmount.setText(String.format("%.1f", this.calculateAmount(item.getValue())) + " " + day.getSymbol());

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

    private float calculateAmount(ObservableList<Transaction> transactions) {
        float amount = 0;

        for (Transaction transaction: transactions) {
            amount += transaction.getAmount();
        }

        return amount;
    }
}
