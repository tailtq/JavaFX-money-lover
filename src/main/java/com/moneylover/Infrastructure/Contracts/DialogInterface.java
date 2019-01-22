package com.moneylover.Infrastructure.Contracts;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public interface DialogInterface {
    default ButtonBar.ButtonData showDeleteDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Delete Transaction");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("You cannot revert this.");

        DialogPane dialog = alert.getDialogPane();
        dialog.getStylesheets().add(getClass().getResource("/assets/css/alerts/transaction-delete.css").toExternalForm());
        dialog.getStyleClass().add("transaction__delete-dialog");

        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        for (Node node: dialog.getChildrenUnmodifiable()) {
            if (node instanceof ButtonBar) {
                ButtonBar buttonBar = (ButtonBar) node;
                buttonBar.getButtons().get(0).getStyleClass().add("delete-button");
            }
        }
        Optional<ButtonType> result = alert.showAndWait();

        return result.get().getButtonData();
    }

    default void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(message);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(cancelButton);

        alert.showAndWait();
    }

    default void createScreen(Parent parent, String title, int v, int v1) {
        Stage stage = new Stage();
        stage.setScene(new Scene(parent, v, v1));
        stage.setMinWidth(v);
        stage.setMinHeight(v1);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    default void closeScene(Event e) {
        Node node = (Node) e.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}