package com.moneylover.app.Transaction;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Time.Entities.CustomDate;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.PagePresenter;
import com.moneylover.app.Category.CategoryPresenter;
import com.moneylover.app.Transaction.View.TransactionDate;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class TransactionPresenter extends PagePresenter {
    private com.moneylover.Modules.Transaction.Controllers.TransactionController transactionController;

    private ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions = FXCollections.observableArrayList();

    private StringProperty handledTransactionId = new SimpleStringProperty();

    private CustomDate date;

    private LocalDate currentDate;

    private CategoryPresenter categoryPresenter;

    public void loadPresenter() throws SQLException, ClassNotFoundException {
        this.currentDate = LocalDate.now();
        this.transactionController = new com.moneylover.Modules.Transaction.Controllers.TransactionController();
        this.categoryPresenter = new CategoryPresenter(this.selectedType, this.selectedCategory, this.selectedSubCategory);
    }

    private void getTransactionsByMonth(Wallet wallet, int month, int year, char operator) throws SQLException {
        this.transactions.clear();
        ArrayList<Transaction> transactions = this.transactionController.listByMonth(wallet.getId(), month, year, operator);
        TransactionPresenter.sortTransactionsByDate(this.transactions, transactions, wallet.getMoneySymbol());
        TransactionPresenter.reservedSortTransactions(this.transactions);
    }

    private void addTransaction(Transaction newTransaction, String moneySymbol) {
        LocalDate newLocalDate = LocalDate.parse(newTransaction.getTransactedAt().toString());
        int transactionDayOfMonth = newLocalDate.getDayOfMonth();
        boolean hasDay = false;

        for (Pair<CustomDate, ObservableList<Transaction>> transaction: transactions) {
            int day = transaction.getKey().getDayOfMonth();

            if (transactionDayOfMonth == day) {
                transaction.getValue().add(newTransaction);
                hasDay = true;
                break;
            }
        }

        if (!hasDay) {
            TransactionPresenter.addNewDay(this.transactions, newTransaction, newLocalDate, moneySymbol);
        }
    }

    public static void sortTransactionsByDate(
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> sortedTransactions,
            ArrayList<Transaction> transactions,
            String moneySymbol
    ) {
        sortedTransactions.clear();

        for (Iterator<Transaction> it = transactions.iterator(); it.hasNext();) {
            Transaction transaction = it.next();
            boolean hasDay = false;
            LocalDate localDate = LocalDate.parse(transaction.getTransactedAt().toString());
            int day = localDate.getDayOfMonth();

            for (Pair<CustomDate, ObservableList<Transaction>> pair: sortedTransactions) {
                if (pair.getKey().getDayOfMonth() == day) {
                    pair.getValue().add(transaction);
                    it.remove();
                    hasDay = true;
                    break;
                }
            }

            if (!hasDay) {
                TransactionPresenter.addNewDay(sortedTransactions, transaction, localDate, moneySymbol);
                it.remove();
            }
        }
    }

    public static void addNewDay(
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions,
            Transaction newTransaction,
            LocalDate newLocalDate,
            String moneySymbol
    ) {
        CustomDate newCustomDate = new CustomDate();
        newCustomDate.setDayOfMonth(newLocalDate.getDayOfMonth());
        newCustomDate.setDayOfWeek(newLocalDate.getDayOfWeek().toString());
        newCustomDate.setMonth(newLocalDate.getMonth().toString());
        newCustomDate.setMonthNumber(newLocalDate.getMonthValue());
        newCustomDate.setYear(newLocalDate.getYear());
        newCustomDate.setSymbol(moneySymbol);
        transactions.add(new Pair<>(newCustomDate, FXCollections.observableArrayList(newTransaction)));
    }

    private static void sortTransactions(ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions) {
        transactions.sort(Comparator.comparingInt(a -> a.getKey().getDayOfMonth()));
    }

    public static void reservedSortTransactions(ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions) {
        transactions.sort(Comparator.comparingInt(a -> a.getKey().getDayOfMonth()));
        FXCollections.reverse(transactions);
    }

    private void handleTransactionId() {
        this.handledTransactionId.addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            int id = Integer.parseInt(newValue.substring(7));
            int i = 0;

            for (Pair<CustomDate, ObservableList<Transaction>> transactionDate: this.transactions) {
                int j = 0;
                for (Transaction transaction: transactionDate.getValue()) {
                    if (transaction.getId() == id) {
                        if (newValue.contains("DELETE-")) {
                            ObservableList<Transaction> transactions = transactionDate.getValue();
                            transactions.remove(j);

                            if (transactions.size() == 0) {
                                this.transactions.remove(i);
                            }
                        } else {
                            try {
                                Transaction updatedTransaction = this.transactionController.getDetail(id);
                                transactionDate.getValue().set(j, updatedTransaction);
                            } catch (SQLException | NotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                        this.loadHeaderWallets();
                        return;
                    }

                    j++;
                }

                i++;
            }
        });
    }

    /*========================== Draw ==========================*/
    @FXML
    private Button leftTimeRange, middleTimeRange, rightTimeRange, selectCategory;

    @FXML
    private ListView listViewDayTransactions;

    @FXML
    private MenuButton selectWallet;

    @FXML
    private TextField textFieldTransactionAmount, textFieldNote;

    @FXML
    private DatePicker datePickerTransactedAt;

    @FXML
    private CheckBox checkBoxIsReported;

    private IntegerProperty walletId = new SimpleIntegerProperty(0);

    private IntegerProperty selectedType = new SimpleIntegerProperty(0);

    private IntegerProperty selectedCategory = new SimpleIntegerProperty(0);

    private IntegerProperty selectedSubCategory = new SimpleIntegerProperty(0);

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException {
        super.setWallets(wallets);
        this.date = new CustomDate(this.currentDate.getMonthValue(), this.currentDate.getYear());
        this.getTransactionsByMonth(wallets.get(0), this.date.getMonthNumber(), this.date.getYear(), '=');
        this.setListViewTransactions();
    }

    private void setListViewTransactions() {
        this.handleTransactionId();
        this.listViewDayTransactions.setItems(this.transactions);
        this.listViewDayTransactions.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                try {
                    return new TransactionDate(handledTransactionId, wallets);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        this.listViewDayTransactions.setFocusTraversable(false);
    }

    @FXML
    private void changeTime(Event e) throws SQLException {
        Node button = (Node) e.getSource();
        int selectedTimeRange = Integer.parseInt(button.getUserData().toString());

        if (this.date.getMonthNumber() == this.currentDate.getMonthValue()
                && this.date.getYear() == this.currentDate.getYear()
                && selectedTimeRange == 1) {
            // TODO: Set current tab text is future, hide
            this.getTransactionsByMonth(this.wallets.get(0), this.date.getMonthNumber(), this.date.getYear(), '>');
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
        int month = this.date.getMonthNumber();
        int year = this.date.getYear();

        if (this.leftTimeRange.getText().equals("THIS MONTH")) {
            this.date.setMonthNumber(this.currentDate.getMonthValue());
        } else if (month == 1 && selectedTimeRange == -1) {
            this.date.setMonthNumber(12);
            this.date.setYear(year - 1);
        } else if (month == 12 && selectedTimeRange == 1) {
            this.date.setMonthNumber(1);
            this.date.setYear(year + 1);
        } else if (selectedTimeRange == -1) {
            this.date.setMonthNumber(--month);
        } else if (selectedTimeRange == 1) {
            this.date.setMonthNumber(++month);
        }

        month = this.date.getMonthNumber();
        year = this.date.getYear();
        this.getTransactionsByMonth(this.wallets.get(0), month, year, '=');
        String displayedMonth = (month >= 10) ? Integer.toString(month) : "0" + month;
        int currentMonth = this.currentDate.getMonthValue();
        int prevMonth = (currentMonth == 1) ? 12 : currentMonth - 1;
        int prevMonth2 = (prevMonth == 1) ? 12 : prevMonth - 1;
        int currentYear = this.currentDate.getYear();
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction/transaction-create.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        this.walletId.set(0);
        this.selectedType.set(0);
        this.selectedCategory.set(0);
        this.selectedSubCategory.set(0);
        TransactionPresenter.loadStaticWallets(this.selectWallet, this.walletId, this.wallets);
        this.categoryPresenter.handleSelectedCategoryId(this.selectedCategory, this.selectCategory, "category");
        this.categoryPresenter.handleSelectedCategoryId(this.selectedSubCategory, this.selectCategory, "subCategory");
        this.datePickerTransactedAt.setValue(LocalDate.now());

        this.createScreen(parent, "Add Transaction", 500, 230);
    }

    public static void loadStaticWallets(MenuButton selectWallet, IntegerProperty walletId, ObservableList<Wallet> wallets) {
        selectWallet.getItems().clear();
        int walletIdInt = walletId.get();

        for (Wallet wallet: wallets) {
            if (wallet.getId() == walletIdInt && walletIdInt != 0) {
                selectWallet.setText(wallet.getName());
                selectWallet.getStyleClass().add("header__wallet");
            }

            MenuItem item = new MenuItem();
            item.setText(wallet.getName());
            item.getStyleClass().add("header__wallet");
            item.setOnAction(e -> {
                MenuItem menuItem = (MenuItem) e.getSource();
                selectWallet.setText(menuItem.getText());
                selectWallet.getStyleClass().add("header__wallet");
                walletId.set(wallet.getId());
            });
            selectWallet.getItems().add(item);
        }
    }

    @FXML
    private void chooseCategory() throws IOException {
        this.categoryPresenter.showCategoryDialog();
    }

    @FXML
    private void storeTransaction(Event event) {
        String amountText = this.textFieldTransactionAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.trim());
        LocalDate transactedAt = this.datePickerTransactedAt.getValue();
        boolean isReported = this.checkBoxIsReported.isSelected();
        int walletId = this.walletId.get();
        int categoryId = this.selectedCategory.get();
        int subCategoryId = this.selectedSubCategory.get();

        if (walletId == 0) {
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
        transaction.setWalletId(walletId);
        transaction.setTypeId(this.selectedType.get());
        transaction.setCategoryId(categoryId);
        transaction.setSubCategoryId(subCategoryId);
        transaction.setAmount(amount);
        transaction.setNote(this.textFieldNote.getText());
        transaction.setTransactedAt(Date.valueOf(transactedAt.toString()));
        transaction.setIsReported((byte) (isReported ? 1 : 0));

        try {
            transaction = this.transactionController.create(transaction);
            this.addTransaction(transaction, this.wallets.get(0).getMoneySymbol());

            this.closeScene(event);
        } catch (SQLException | NotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }
}
