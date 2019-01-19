package com.moneylover.app.controllers.Pages.Transaction;

import com.jfoenix.controls.JFXPopup;
import com.moneylover.Modules.Transaction.Controllers.TransactionController;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.app.controllers.Contracts.DialogInterface;
import javafx.beans.property.IntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;

public class TransactionCell extends ListCell<Transaction> implements DialogInterface {
    private HBox transactionCell;

    private Transaction transaction;

    private TransactionController transactionController;

    TransactionCell(IntegerProperty deletedTransactionId) throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader transactionCellLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/transaction/transaction-cell.fxml"));
        transactionCellLoader.setController(new TransactionCellController(deletedTransactionId));
        transactionCell = transactionCellLoader.load();
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
    private MenuButton option;

    @Override
    protected void updateItem(Transaction item, boolean empty) {
        if (empty) {
            setGraphic(null);
        } else {
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
        this.transaction = item;
    }
}
