package com.moneylover.app.Transaction;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.PagePresenter;
import com.moneylover.app.Category.CategoryPresenter;
import com.moneylover.app.Transaction.View.TransactionCell;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Iterator;

public class TransactionPresenter extends PagePresenter {
    private com.moneylover.Modules.Transaction.Controllers.TransactionController transactionController;

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private StringProperty handledTransactionId = new SimpleStringProperty();

    private LocalDate currentDate = LocalDate.now();

    private LocalDate tabDate = LocalDate.now();

    private CategoryPresenter categoryPresenter;

    public void loadPresenter() throws SQLException, ClassNotFoundException {
        this.transactionController = new com.moneylover.Modules.Transaction.Controllers.TransactionController();
        this.categoryPresenter = new CategoryPresenter(this.selectedType, this.selectedCategory, this.selectedSubCategory);
    }

    private void getTransactionsByDate(int walletId, LocalDate date, char operator) throws SQLException {
        this.transactions.clear();
        this.transactions.addAll(
                this.transactionController.listByMonth(walletId, date, operator)
        );
    }

    private static void _addNewTransaction(ObservableList<Transaction> transactions, Transaction newTransaction) {
        transactions.add(newTransaction);
        TransactionPresenter.reservedSortTransactions(transactions);
    }

    public static void sortTransactions(ObservableList<Transaction> transactions) {
        transactions.sort(
                Comparator.comparingInt(e -> ((Transaction) e).getTransactedAt().getDayOfMonth())
                        .thenComparingInt(e -> ((Transaction) e).getId())
        );
    }

    public static void reservedSortTransactions(ObservableList<Transaction> transactions) {
        TransactionPresenter.sortTransactions(transactions);
        FXCollections.reverse(transactions);
    }

    private void handleTransactionId() {
        this.handledTransactionId.addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            int id = Integer.parseInt(newValue.substring(7));

            for (Iterator<Transaction> it = this.transactions.iterator(); it.hasNext();) {
                Transaction transaction = it.next();

                if (transaction.getId() == id) {
                    it.remove();
                    break;
                }
            }

            if (newValue.contains("UPDATE")) {
                try {
                    Transaction updatedTransaction = this.transactionController.getDetail(id);
                    LocalDate transactedAt = updatedTransaction.getTransactedAt();

                    if (updatedTransaction.getWalletId() == this.getWalletIndexId()
                            && transactedAt.getMonthValue() == this.tabDate.getMonthValue()
                            && transactedAt.getYear() == this.tabDate.getYear()) {
                        TransactionPresenter._addNewTransaction(this.transactions, updatedTransaction);
                    }
                } catch (SQLException | NotFoundException e) {
                    e.printStackTrace();
                }
            }

            this._calculateStatistic();
        });
    }

    /*========================== Draw ==========================*/
    @FXML
    private Label labelInflow, labelOutflow, labelRemainingAmount;

    @FXML
    private Button leftTimeRange, middleTimeRange, rightTimeRange, selectCategory;

    @FXML
    private ListView listViewTransactions;

    @FXML
    private MenuButton selectWallet;

    @FXML
    private TextField textFieldTransactionAmount, textFieldNote;

    @FXML
    private DatePicker datePickerTransactedAt;

    @FXML
    private CheckBox checkBoxIsNotReported;

    private IntegerProperty walletId = new SimpleIntegerProperty(0);

    private IntegerProperty selectedType = new SimpleIntegerProperty(0);

    private IntegerProperty selectedCategory = new SimpleIntegerProperty(0);

    private IntegerProperty selectedSubCategory = new SimpleIntegerProperty(0);

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException {
        super.setWallets(wallets);
        this.getTransactionsByDate(this.getWallet().getId(), this.tabDate, '=');
        this._calculateStatistic();
        this._setListViewTransactions();
    }

    private void _calculateStatistic() {
        float inflow = 0, outflow = 0;

        for (Transaction transaction: this.transactions) {
            if (transaction.getAmount() > 0) {
                inflow += transaction.getAmount();
            } else {
                outflow += transaction.getAmount();
            }
        }

        this.labelInflow.setText(this.toMoneyString(inflow));
        this.labelOutflow.setText(this.toMoney(outflow));
        this.labelRemainingAmount.setText(this.toMoneyString(inflow + outflow));
    }

    private void _setListViewTransactions() {
        this.handleTransactionId();

        if (this.transactions.size() == 0) {
            this.listViewTransactions.setPlaceholder(new Label("No Transaction In List"));
        }

        TransactionPresenter.listTransactions(
                this.listViewTransactions,
                this.transactions,
                this.wallets,
                this.handledTransactionId
        );
    }

    public static void listTransactions(
            ListView listView,
            ObservableList<Transaction> transactions,
            ObservableList<Wallet> wallets,
            StringProperty handledTransactionId
    ) {
        listView.setItems(transactions);
        listView.setCellFactory(new Callback<ListView, ListCell>() {
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
    }

    @FXML
    private void changeTime(Event e) throws SQLException {
        Node button = (Node) e.getSource();
        int selectedTimeRange = Integer.parseInt(button.getUserData().toString());

        if (this.tabDate.getMonthValue() == this.currentDate.getMonthValue()
                && this.tabDate.getYear() == this.currentDate.getYear()
                && selectedTimeRange == 1) {
            // TODO: Set current tab text is future, hide
            this.getTransactionsByDate(this.getWallet().getId(), this.tabDate, '>');
            this.leftTimeRange.setText("THIS MONTH");
            this.middleTimeRange.setText("FUTURE");
            this.rightTimeRange.setVisible(false);
        } else {
            this.rightTimeRange.setVisible(true);
            this._setDay(selectedTimeRange);
        }

        this._calculateStatistic();
    }

    private void _setDay(int selectedTimeRange) throws SQLException {
        int month = this.tabDate.getMonthValue();
        int year = this.tabDate.getYear();

        if (this.leftTimeRange.getText().equals("THIS MONTH")) {
            this.tabDate = LocalDate.parse(year + "-" + (month > 9 ? month : "0" + month) + "-01");
        } else if (month == 1 && selectedTimeRange == -1) {
            this.tabDate = LocalDate.parse((year - 1) + "-12-01");
        } else if (month == 12 && selectedTimeRange == 1) {
            this.tabDate = LocalDate.parse((year + 1) + "-01-01");
        } else if (selectedTimeRange == -1) {
            month--;
            this.tabDate = LocalDate.parse(year + "-" + (month > 9 ? month : "0" + month) + "-01");
        } else if (selectedTimeRange == 1) {
            month++;
            this.tabDate = LocalDate.parse(year + "-" + (month > 9 ? month : "0" + month) + "-01");
        }

        this.getTransactionsByDate(this.getWallet().getId(), this.tabDate, '=');
        month = this.tabDate.getMonthValue();
        year = this.tabDate.getYear();
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
            this.leftTimeRange.setText(this._getTimeRangeText(month - 1, year));
            this.middleTimeRange.setText("LAST MONTH");
            this.rightTimeRange.setText("THIS MONTH");
        } else if (year == prevYear2 && month == prevMonth2) {
            this.leftTimeRange.setText(this._getTimeRangeText(month - 1, year));
            this.middleTimeRange.setText(displayedMonth + "/" + year);
            this.rightTimeRange.setText("LAST MONTH");
        } else {
            this.leftTimeRange.setText(this._getTimeRangeText(month - 1, year));
            this.middleTimeRange.setText(displayedMonth + "/" + year);
            this.rightTimeRange.setText(this._getTimeRangeText(month + 1, year));
        }
    }

    private String _getTimeRangeText(int month, int year) {
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction/transaction-save.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        this.walletId.set(0);
        this.selectedCategory.set(0);
        this.selectedSubCategory.set(0);
        PagePresenter.loadStaticWallets(this.selectWallet, this.walletId, this.wallets);
        this.categoryPresenter.handleSelectedCategoryId(this.selectedCategory, this.selectCategory, "category");
        this.categoryPresenter.handleSelectedCategoryId(this.selectedSubCategory, this.selectCategory, "subCategory");
        this.datePickerTransactedAt.setValue(LocalDate.now());

        this.createScreen(parent, "Add Transaction", 500, 230);
    }

    @FXML
    private void chooseCategory() throws IOException {
        this.categoryPresenter.showCategoryDialog();
    }

    @FXML
    private void saveTransaction(Event event) {
        String amountText = this.textFieldTransactionAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.trim());
        LocalDate transactedAt = this.datePickerTransactedAt.getValue();
        boolean isNotReported = this.checkBoxIsNotReported.isSelected();
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
        transaction.setTransactedAt(transactedAt);
        transaction.setIsNotReported(isNotReported);

        try {
            transaction = this.transactionController.create(transaction);
            transactedAt = transaction.getTransactedAt();

            if (transaction.getWalletId() == this.getWalletIndexId()
                    && transactedAt.getMonthValue() == this.tabDate.getMonthValue()
                    && transactedAt.getYear() == this.tabDate.getYear()) {
                TransactionPresenter._addNewTransaction(this.transactions, transaction);
                this._calculateStatistic();
            }

            this.closeScene(event);
        } catch (SQLException | NotFoundException | ClassNotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }
}
