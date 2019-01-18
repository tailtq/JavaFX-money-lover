package com.moneylover.app.controllers.Pages.Transaction;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Time.Entities.Day;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.controllers.Contracts.UseCategoryInterface;
import com.moneylover.app.controllers.PageController;
import javafx.beans.property.*;
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
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public class TransactionController extends PageController implements UseCategoryInterface {
    private com.moneylover.Modules.Transaction.Controllers.TransactionController transactionController;

    private ObservableList<Pair<Day, ObservableList<Transaction>>> transactions = FXCollections.observableArrayList();

    private Day day;

    private LocalDate currentDay;

    private CategoryController categoryController;

    public TransactionController(BooleanProperty changeWallet) throws SQLException, ClassNotFoundException {
        this.changeWallet = changeWallet;
        this.transactionController = new com.moneylover.Modules.Transaction.Controllers.TransactionController();
        this.currentDay = LocalDate.now();
        this.categoryController = new CategoryController(this.selectedType, this.selectedCategory, this.selectedSubCategory);
    }

    private void getTransactionsByMonth(Wallet wallet, int month, int year, char operator) throws SQLException {
        this.transactions.clear();
        ArrayList<Transaction> transactions = this.transactionController.list(wallet.getId(), month, year, operator);

        for (Transaction transaction: transactions) {
            boolean hasDay = false;
            LocalDate localDate = LocalDate.parse(transaction.getTransactedAt().toString());
            int day = localDate.getDayOfMonth();

            for (Pair<Day, ObservableList<Transaction>> pair: this.transactions) {
                if (pair.getKey().getDayOfMonth() == day) {
                    pair.getValue().add(transaction);
                    hasDay = true;
                }
            }

            if (!hasDay) {
                this.addNewDay(transaction, localDate, wallet);
            }
        }

        // TODO: should refactor with reversed sort
        this.sortTransactions();
    }

    private void addTransaction(Transaction newTransaction, Wallet wallet) {
        LocalDate newLocalDate = LocalDate.parse(newTransaction.getTransactedAt().toString());
        int newDayOfMonth = newLocalDate.getDayOfMonth();
        boolean hasDay = false;

        for (Pair<Day, ObservableList<Transaction>> transaction: transactions) {
            int day = transaction.getKey().getDayOfMonth();

            if (newDayOfMonth == day) {
                transaction.getValue().add(newTransaction);
                hasDay = true;
                break;
            }
        }

        if (!hasDay) {
            this.addNewDay(newTransaction, newLocalDate, wallet);
        }
    }

    private void addNewDay(Transaction newTransaction, LocalDate newLocalDate, Wallet wallet) {
        Day newDay = new Day();
        newDay.setDayOfMonth(newLocalDate.getDayOfMonth());
        newDay.setDayOfWeek(newLocalDate.getDayOfWeek().toString());
        newDay.setMonth(newLocalDate.getMonth().toString());
        newDay.setSymbol(wallet.getMoneySymbol());
        this.transactions.add(new Pair<>(newDay, FXCollections.observableArrayList(newTransaction)));
    }

    private void sortTransactions() {
        this.transactions.sort(Comparator.comparingInt(a -> a.getKey().getDayOfMonth()));
        FXCollections.reverse(this.transactions);
    }

    /*========================== Draw ==========================*/
    @FXML
    private Button leftTimeRange, middleTimeRange, rightTimeRange, selectCategory;

    @FXML
    private ListView transactionDays;

    @FXML
    private MenuButton selectWallet;

    @FXML
    private TextField textFieldTransactionAmount, textFieldNote;

    @FXML
    private DatePicker datePickerTransactedAt;

    @FXML
    private CheckBox checkBoxIsReported;

    private int walletId = 0;

    private IntegerProperty selectedType = new SimpleIntegerProperty(0);

    private IntegerProperty selectedCategory = new SimpleIntegerProperty(0);

    private IntegerProperty selectedSubCategory = new SimpleIntegerProperty(0);

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
        this.day = new Day(this.currentDay.getMonthValue(), this.currentDay.getYear());
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
    private void createTransaction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction-create.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        this.loadWallets();
        this.categoryController.handleSelectedCategoryId(this.selectedCategory, this.selectCategory, "category");
        this.categoryController.handleSelectedCategoryId(this.selectedSubCategory, this.selectCategory, "subCategory");
        this.datePickerTransactedAt.setValue(LocalDate.now());
        this.selectedType.set(0);
        this.selectedCategory.set(0);
        this.selectedSubCategory.set(0);

        this.createScreen(parent, "Add Transaction", 500, 230);
    }

    private void loadWallets() {
        this.selectWallet.getItems().clear();

        for (Wallet wallet: this.wallets) {
            MenuItem item = new MenuItem();
            item.setText(wallet.getName());
            item.getStyleClass().add("header__wallet");
            item.setOnAction(e -> {
                MenuItem menuItem = (MenuItem) e.getSource();
                this.selectWallet.setText(menuItem.getText());
                this.selectWallet.getStyleClass().add("header__wallet");
                this.walletId = wallet.getId();
            });
            this.selectWallet.getItems().add(item);
        }
    }

    @FXML
    private void chooseCategory() throws IOException {
        this.categoryController.showCategoryDialog();
    }

    @FXML
    private void storeTransaction(Event event) {
        String amountText = this.textFieldTransactionAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.trim());
        LocalDate transactedAt = this.datePickerTransactedAt.getValue();
        boolean isReported = this.checkBoxIsReported.isSelected();
        int categoryId = this.selectedCategory.get();
        int subCategoryId = this.selectedSubCategory.get();

        if (this.walletId == 0) {
            this.showErrorDialog("Wallet is not selected");
            return;
        }
        if (categoryId == 0) {
            this.showErrorDialog("Category is not selected");
            return;
        }
        if (amount <= 0) {
            this.showErrorDialog("Amount is not valid");
            return;
        }

        Transaction transaction = new Transaction();
        transaction.setWalletId(this.walletId);
        transaction.setTypeId(this.selectedType.get());
        transaction.setCategoryId(categoryId);
        transaction.setAmount(amount);
        transaction.setNote(this.textFieldNote.getText());
        transaction.setTransactedAt(Date.valueOf(transactedAt.toString()));
        transaction.setIsReported((byte) (isReported ? 1 : 0));

        if (subCategoryId != 0) {
            transaction.setSubCategoryId(subCategoryId);
        }

        try {
            transaction = this.transactionController.create(transaction);
            this.addTransaction(transaction, this.wallets.get(0));

            this.closeScene(event);
        } catch (SQLException | NotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }

//    @FXML
//    public void showFriendDialog(Event e) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/choose-friends.fxml"));
//        fxmlLoader.setController(this);
//        Parent parent = fxmlLoader.load();
//
//        this.createScreen(parent, "Choose Friend", 300, 200);
//    }
}
