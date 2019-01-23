package com.moneylover.app.Report.View;

import com.moneylover.Modules.Time.Entities.CustomDate;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportCell extends ListCell<Pair<CustomDate, ObservableList<Transaction>>> {
    private HBox reportCell;

    public ReportCell() throws IOException {
        FXMLLoader reportCellLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/report/report-cell.fxml"));
        reportCellLoader.setController(this);
        this.reportCell = reportCellLoader.load();
    }

    /*========================== Draw ==========================*/
    @FXML
    private Label labelDateTime;

    @FXML
    private Label labelIncome;

    @FXML
    private Label labelOutcome;

    @FXML
    private Label labelAmount;

    protected void updateItem(Pair<CustomDate, ObservableList<Transaction>> item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            return;
        }

        ObservableList<Transaction> transactions = item.getValue();
        float income = 0;
        float outcome = 0;

        for (Transaction transaction: transactions) {
            float transactionAmount = transaction.getAmount();

            if (transactionAmount < 0) {
                outcome += transactionAmount;
            } else {
                income += transactionAmount;
            }
        }

        float amount = income - Math.abs(outcome);
        CustomDate date = item.getKey();
        String month = (date.getMonthNumber() < 10) ? "0" + date.getMonthNumber() : Integer.toString(date.getMonthNumber());
        String day = (date.getDayOfMonth() < 10) ? "0" + date.getDayOfMonth() : Integer.toString(date.getDayOfMonth());
        String dateString = date.getYear() + "-" + month + "-" + day;
        String amountString = (amount > 0) ? "+" + amount : Float.toString(amount);
        LocalDate localDate = LocalDate.parse(dateString);

        labelDateTime.setText(localDate.format(DateTimeFormatter.ofPattern("EEEE Y/MM/dd")));
        labelIncome.setText("+" + income);
        labelOutcome.setText(Float.toString(outcome));
        labelAmount.setText(amountString);
        setGraphic(this.reportCell);
    }
}
