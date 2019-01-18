package com.moneylover.app.controllers.Pages.Transaction;

import com.moneylover.Modules.Transaction.Entities.Transaction;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
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

    @FXML
    private Label labelAmount;

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
    }

    @FXML
    public void editTransaction(Event e) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction-create.fxml"));
//        fxmlLoader.setController(this);
//        Parent parent = fxmlLoader.load();
//
//        this.createScreen(parent, "Edit Transaction", 500, 230);
    }

    @FXML
    public void deleteTransaction(Event e) {
//        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
//        if (buttonData == ButtonBar.ButtonData.YES) {
//            System.out.println("Yes");
//        }
    }
}
