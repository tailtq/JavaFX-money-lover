package com.moneylover.app.Debt;

import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Transaction.Controllers.TransactionController;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.Category.CategoryPresenter;
import com.moneylover.app.PagePresenter;
import com.moneylover.app.Transaction.TransactionPresenter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class DebtPresenter extends PagePresenter {
    private TransactionController transactionController;

    private ObservableList<Transaction> debts = FXCollections.observableArrayList();

    private ObservableList<Transaction> loans = FXCollections.observableArrayList();

    private StringProperty handledDebtId = new SimpleStringProperty();

    private CategoryPresenter categoryPresenter;

    @Override
    public void loadPresenter() throws SQLException, ClassNotFoundException {
        this.transactionController = new TransactionController();
        this.categoryPresenter = new CategoryPresenter(this.selectedType, this.selectedCategory, this.selectedSubCategory);
        this.categoryPresenter.setOnlyDebtCategories(true);
    }

    /*========================== Draw ==========================*/
    @FXML
    private TabPane tabPaneDebts;

    @FXML
    private ListView listViewDebts;

    @FXML
    private ListView listViewLoans;

    private IntegerProperty selectWallet = new SimpleIntegerProperty(0);

    private IntegerProperty selectedType = new SimpleIntegerProperty(0);

    private IntegerProperty selectedCategory = new SimpleIntegerProperty(0);

    private IntegerProperty selectedSubCategory = new SimpleIntegerProperty(0);

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException {
        super.setWallets(wallets);
        this._getDebtTransaction(wallets.get(this.walletIndex.get()).getId());
        this._setListViewDebts();
    }

    private void _setListViewDebts() {
        this.handleDebtId();
        TransactionPresenter.listTransactions(
                this.listViewDebts,
                this.debts,
                this.wallets,
                this.handledDebtId
        );
        TransactionPresenter.listTransactions(
                this.listViewLoans,
                this.loans,
                this.wallets,
                this.handledDebtId
        );
    }

    private void _getDebtTransaction(int walletId) throws SQLException {
        ArrayList<Transaction> transactions = this.transactionController.listDebts(walletId);
        this.debts.clear();
        this.loans.clear();

        for (Transaction transaction: transactions) {
            if (transaction.getCategoryName().equals(CommonConstants.CATEGORY_DEBT)) {
                this.debts.add(transaction);
            } else {
                this.loans.add(transaction);
            }
        }
    }

    private void handleDebtId() {
        this.handledDebtId.addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            try {
                this.handleTransactionId(this.debts, newValue);
                this.handleTransactionId(this.loans, newValue);
            } catch (NotFoundException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleTransactionId(ObservableList<Transaction> transactions, String value) throws NotFoundException, SQLException {
        int i = 0, id = Integer.parseInt(value.substring(7));

        for (Iterator<Transaction> it = transactions.iterator(); it.hasNext();) {
            Transaction transaction = it.next();

            if (transaction.getId() == id) {
                if (value.contains("UPDATE")) {
                    transactions.set(i, this.transactionController.getDetail(transaction.getId()));
                } else {
                    it.remove();
                }
                break;
            }

            i++;
        }
    }

    @FXML
    private void changeTab(Event e) {
        this.activeTab(e, this.tabPaneDebts);
    }

    @FXML
    private void createDebt() {

    }
}
