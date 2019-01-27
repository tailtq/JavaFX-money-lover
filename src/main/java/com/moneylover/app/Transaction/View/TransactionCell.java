package com.moneylover.app.Transaction.View;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Transaction.Controllers.TransactionController;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.Infrastructure.Contracts.DialogInterface;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.app.Category.CategoryPresenter;
import com.moneylover.app.Transaction.TransactionPresenter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransactionCell extends ListCell<Transaction> implements DialogInterface {
    private HBox transactionCell;

    private Transaction transaction;

    private ObservableList<Wallet> wallets;

    private TransactionController transactionController;

    private CategoryPresenter categoryPresenter;

    private StringProperty handledTransactionId;

    public TransactionCell(StringProperty handledTransactionId) throws IOException, SQLException, ClassNotFoundException {
        this.handledTransactionId = handledTransactionId;
        this.transactionController = new TransactionController();
        this.categoryPresenter = new CategoryPresenter(this.selectedType, this.selectedCategory, this.selectedSubCategory);
        this.loadCell();
    }

    private void loadCell() throws IOException {
        FXMLLoader transactionCellLoader = new FXMLLoader(
                getClass().getResource("/com/moneylover/pages/transaction/transaction-cell.fxml")
        );
        transactionCellLoader.setController(this);
        transactionCell = transactionCellLoader.load();
    }

    public void setWallets(ObservableList<Wallet> wallets) {
        this.wallets = wallets;
    }

    /*========================== Draw ==========================*/
    @FXML
    private ImageView imageTransactionCategory;

    @FXML
    private Label labelTransactionCategoryName;

    @FXML
    private Label labelTransactionNote;

    @FXML
    private Label labelAmount;

    @FXML
    private Button buttonOptions;

    private IntegerProperty walletId = new SimpleIntegerProperty(0);

    @FXML
    private Button selectCategory;

    @FXML
    private MenuButton selectWallet;

    @FXML
    private TextField textFieldTransactionAmount, textFieldNote;

    @FXML
    private DatePicker datePickerTransactedAt;

    @FXML
    private CheckBox checkBoxIsReported;

    private IntegerProperty selectedType = new SimpleIntegerProperty(0);

    private IntegerProperty selectedCategory = new SimpleIntegerProperty(0);

    private IntegerProperty selectedSubCategory = new SimpleIntegerProperty(0);

    @Override
    protected void updateItem(Transaction item, boolean empty) {
        if (empty) {
            setGraphic(null);
            return;
        }

        this.transaction = item;
        String text = item.getSubCategoryName();
        String imageUrl = "/assets/images/categories/" + item.getSubCategoryIcon() + ".png";

        if (text == null || text.equals("")) {
            text = item.getCategoryName();
            imageUrl = "/assets/images/categories/" + item.getCategoryIcon() + ".png";
        }

        this.imageTransactionCategory.setImage(new Image(imageUrl));
        this.labelTransactionCategoryName.setText(text);
        this.labelTransactionNote.setText(item.getNote());
        this.labelAmount.setText(Float.toString(item.getAmount()));
        setGraphic(this.transactionCell);
    }

    void setDisableOptions(boolean disableOptions) {
        if (disableOptions) {
            this.buttonOptions.setDisable(true);
        }
    }

    @FXML
    private void showPopup(Event e) throws IOException {
        this.addEditPopup((Node) e.getSource());
    }

    @FXML
    private void chooseCategory() throws IOException {
        this.categoryPresenter.showCategoryDialog();
    }

    @FXML
    private void edit() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction/transaction-edit.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        this.loadTransactionData();
        this.createScreen(parent, "Edit Transaction", 500, 230);
    }

    private void loadTransactionData() {
        this.walletId.set(this.transaction.getWalletId());
        this.textFieldNote.setText(this.transaction.getNote());
        this.textFieldTransactionAmount.setText(Float.toString(this.transaction.getAmount()));
        /* TODO: reload update after transaction cell is edit again */
        this.walletId.set(this.transaction.getWalletId());
        this.selectedCategory.set(0);
        this.selectedSubCategory.set(0);
        TransactionPresenter.loadStaticWallets(this.selectWallet, this.walletId, this.wallets);
        this.categoryPresenter.handleSelectedCategoryId(this.selectedCategory, this.selectCategory, "category");
        this.categoryPresenter.handleSelectedCategoryId(this.selectedSubCategory, this.selectCategory, "subCategory");
        this.selectedType.set(this.transaction.getTypeId());
        this.selectedCategory.set(this.transaction.getCategoryId());
        this.selectedSubCategory.set(this.transaction.getSubCategoryId());
        this.datePickerTransactedAt.setValue(LocalDate.parse(this.transaction.getTransactedAt().toString()));
    }

    @FXML
    private void updateTransaction(Event event) {
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
            int id = this.transaction.getId();
            this.transactionController.update(transaction, id);
            this.handledTransactionId.set(null);
            this.handledTransactionId.set("UPDATE-" + id);

            this.closeScene(event);
        } catch (SQLException | NotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }

    @FXML
    private void delete() {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            try {
                int id = this.transaction.getId();
                this.transactionController.delete(id);
                this.handledTransactionId.set("DELETE-" + id);
            } catch (SQLException e1) {
                e1.printStackTrace();
                this.showErrorDialog("An error has occurred");
            }
        }
    }

    @FXML
    public void closeScene(Event e) {
        Node node = (Node) e.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
