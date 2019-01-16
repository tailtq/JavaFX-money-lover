package com.moneylover.app.controllers.Pages.Transaction;

import com.moneylover.Modules.Time.Entities.Day;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.controllers.Contracts.UseCategoryInterface;
import com.moneylover.app.controllers.PageController;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public class TransactionController extends PageController implements UseCategoryInterface {
    private com.moneylover.Modules.Transaction.Controllers.TransactionController transactionController;

    private ObservableList<Pair<Day, ObservableList<Transaction>>> transactions = FXCollections.observableArrayList();

    private Day day;

    private LocalDate currentDay;

    public TransactionController(BooleanProperty changeWallet) throws SQLException, ClassNotFoundException {
        this.changeWallet = changeWallet;
        this.transactionController = new com.moneylover.Modules.Transaction.Controllers.TransactionController();
        this.currentDay = LocalDate.now();
    }

    private void getTransactionsByMonth(Wallet wallet, int month, int year, char operator) throws SQLException {
        this.transactions.clear();
        ArrayList<Transaction> transactions = this.transactionController.list(wallet.getId(), month, year, operator);

        for (Transaction transaction: transactions) {
            boolean hasDay = false;
            LocalDate dayObj = LocalDate.parse(transaction.getTransactedAt().toString());
            int day = dayObj.getDayOfMonth();

            for (Pair<Day, ObservableList<Transaction>> pair: this.transactions) {
                if (pair.getKey().getDayOfMonth() == day) {
                    pair.getValue().add(transaction);
                    hasDay = true;
                }
            }

            if (!hasDay) {
                Day newDay = new Day(
                        day,
                        dayObj.getDayOfWeek().toString(),
                        dayObj.getMonth().toString(),
                        wallet.getMoneySymbol()
                );
                this.transactions.add(
                        new Pair<>(newDay, FXCollections.observableArrayList(transaction))
                );
            }
        }

        // TODO: should refactor with reversed sort
        this.transactions.sort(Comparator.comparingInt(a -> a.getKey().getDayOfMonth()));
        FXCollections.reverse(this.transactions);
    }

    /*========================== Draw ==========================*/
    @FXML
    private TabPane categoriesTabPane;

    @FXML
    private Button leftTimeRange, middleTimeRange, rightTimeRange;

    @FXML
    private ListView transactionDays;

//    @FXML
//    private VBox transactionContent, transactionTimes;

//    @FXML
//    private Label inflow;

    @FXML
    private TextField amount;

    @FXML
    private DatePicker transactedAt;

//    @FXML
//    private TreeView categoriesView;

    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/transaction/transaction.fxml"));
        fxmlLoader.setController(this);
        VBox vBox = fxmlLoader.load();

        return vBox;
    }

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException {
        super.setWallets(wallets);
        this.day = new Day(
                this.currentDay.getMonthValue(),
                this.currentDay.getYear()
        );
        this.getTransactionsByMonth(wallets.get(0), this.day.getMonthNumber(), this.day.getYear(), '=');
        this.setListViewTransactions();
    }

    private void setListViewTransactions() {
        this.transactionDays.setItems(this.transactions);
        this.transactionDays.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                try {
                    return new TransactionDate();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    @FXML
    private void changeTime(Event e) throws SQLException {
        Node button = (Node) e.getSource();
        int selectedTimeRange = Integer.parseInt(button.getUserData().toString());

        if (this.day.getMonthNumber() == this.currentDay.getMonthValue()
                && this.day.getYear() == this.currentDay.getYear()
                && selectedTimeRange == 1) {
            // TODO: Set current tab text is future, hide
            this.getTransactionsByMonth(this.wallets.get(0), this.day.getMonthNumber(), this.day.getYear(), '>');
            this.leftTimeRange.setText("THIS MONTH");
            this.middleTimeRange.setText("FUTURE");
            this.rightTimeRange.setVisible(false);
            return;
        } else {
            this.rightTimeRange.setVisible(true);
        }

        this.setDay(selectedTimeRange);
    }

    private void setDay(int selectedTimeRange) throws SQLException {
        int month = this.day.getMonthNumber();
        int year = this.day.getYear();

        if (month == 1 && selectedTimeRange == -1) {
            this.day.setMonthNumber(12);
            this.day.setYear(year - 1);
        } else if (month == 12 && selectedTimeRange == 1) {
            this.day.setMonthNumber(1);
            this.day.setYear(year + 1);
        } else if (selectedTimeRange == -1) {
            this.day.setMonthNumber(--month);
        } else if (selectedTimeRange == 1) {
            this.day.setMonthNumber(++month);
        }

        month = this.day.getMonthNumber();
        year = this.day.getYear();
        this.getTransactionsByMonth(this.wallets.get(0), month, year, '=');
        String displayedMonth = (month >= 10) ? Integer.toString(month) : "0" + month;

        int currentMonth = this.currentDay.getMonthValue();
        int prevMonth = (currentMonth == 1) ? 12 : currentMonth - 1;
        int prevMonth2 = (prevMonth == 1) ? 12 : prevMonth - 1;
        int currentYear = this.currentDay.getYear();
        int prevYear = (currentMonth == 1) ? currentYear - 1 : currentYear;
        int prevYear2 = (prevMonth == 1) ? prevYear - 1 : prevYear;

        if (year == currentYear && month == currentMonth) {
            this.leftTimeRange.setText("LAST MONTH");
            this.middleTimeRange.setText("THIS MONTH");
            this.rightTimeRange.setText("FUTURE");
        } else if (year == prevYear && month == prevMonth) {
            this.leftTimeRange.setText(this.getTimeRangeText(month - 1, year));
            this.middleTimeRange.setText("LAST MONTH");
            this.rightTimeRange.setText("THIS MONTH");
        } else if (year == prevYear2 && month == prevMonth2) {
            this.leftTimeRange.setText(this.getTimeRangeText(month - 1, year));
            this.middleTimeRange.setText(displayedMonth + "/" + year);
            this.rightTimeRange.setText("LAST MONTH");
        } else {
            this.leftTimeRange.setText(this.getTimeRangeText(month - 1, year));
            this.middleTimeRange.setText(displayedMonth + "/" + year);
            this.rightTimeRange.setText(this.getTimeRangeText(month + 1, year));
        }
    }

    private String getTimeRangeText(int month, int year) {
        String text;
        if (month == 0) {
            text = "12/" + (year - 1);
        } else if (month == 13) {
            text = "01/" + (year + 1);
        } else {
            text = (month >= 10) ? Integer.toString(month) : "0" + month;
            text += "/" + year;
        }

        return text;
    }

    @FXML
    public void createTransaction(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction-create.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        this.transactedAt.setValue(LocalDate.now());

        this.createScreen(parent, "Add Transaction", 500, 230);
    }

    @FXML
    public void editTransaction(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction-create.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        this.createScreen(parent, "Edit Transaction", 500, 230);
    }

    @FXML
    public void deleteTransaction(Event e) {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            System.out.println("Yes");
        }
    }

    @FXML
    private void chooseCategory(Event e) throws IOException {
        this.showCategoryDialog(e);
    }

//    @FXML
//    public void showFriendDialog(Event e) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/choose-friends.fxml"));
//        fxmlLoader.setController(this);
//        Parent parent = fxmlLoader.load();
//
//        this.createScreen(parent, "Choose Friend", 300, 200);
//    }

    @FXML
    public void changeTab(Event e) {
        this.activeTab(e, this.categoriesTabPane);
    }
}
