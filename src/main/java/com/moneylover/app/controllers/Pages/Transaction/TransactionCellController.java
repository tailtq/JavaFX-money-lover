package com.moneylover.app.controllers.Pages.Transaction;

import com.jfoenix.controls.JFXPopup;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;

public class TransactionCellController {


    @FXML
    private void showPopup(Event e) throws IOException {
        System.out.println("test");
        Node button = (Node) e.getSource();
        FXMLLoader optionalButtonsLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/optional-buttons.fxml"));
        optionalButtonsLoader.setController(this);
        HBox container = optionalButtonsLoader.load();

        JFXPopup popup = new JFXPopup(container);
        popup.show(button, JFXPopup.PopupVPosition.BOTTOM, JFXPopup.PopupHPosition.LEFT, 10, 10);
    }

    @FXML
    public void edit(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction-create.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

//        this.createScreen(parent, "Edit Transaction", 500, 230);
    }

    @FXML
    public void delete() {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            try {
                int id = this.transaction.getId();
                this.transactionController.delete(id);
                this.deletedTransactionId.set(id);
            } catch (SQLException e1) {
                e1.printStackTrace();
                this.showErrorDialog("An error has occurred");
            }
        }
    }
}
