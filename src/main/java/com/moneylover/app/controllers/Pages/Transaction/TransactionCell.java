package com.moneylover.app.controllers.Pages.Transaction;

import com.moneylover.Modules.Transaction.Entities.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TransactionCell extends ListCell<Transaction> {
    private HBox transactionCell;

    TransactionCell() throws IOException {
        FXMLLoader transactionCellLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/transaction/transaction-cell.fxml"));
        transactionCellLoader.setController(this);
        transactionCell = transactionCellLoader.load();
    }

    /*========================== Draw ==========================*/
    @FXML
    private ImageView imageTransactionCategory;

    @FXML
    private Label labelTransactionCategoryName;

    @FXML
    private Label labelTransactionNote;

    @Override
    protected void updateItem(Transaction item, boolean empty) {
        if (empty) {
            setGraphic(null);
        } else {
            String text = item.getSubCategoryName();
            String imageUrl = "/assets/images/categories/" + item.getSubCategoryIcon() + ".png";
            if (text.equals("")) {
                text = item.getCategoryName();
                imageUrl = "/assets/images/categories/" + item.getCategoryName() + ".png";
            }

            this.imageTransactionCategory.setImage(new Image(imageUrl));
            this.labelTransactionCategoryName.setText(text);
            this.labelTransactionNote.setText(item.getNote());
//            labelTransactionAmount.setText(item);
            setGraphic(this.transactionCell);
        }
    }
}
