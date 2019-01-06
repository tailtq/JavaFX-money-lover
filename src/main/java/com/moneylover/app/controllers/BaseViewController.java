package com.moneylover.app.controllers;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Optional;

public class BaseViewController {
    public ButtonBar.ButtonData showDeleteDialog() {
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

    protected boolean activeButton(Node button) {
        ObservableList<Node> nodes = button.getParent().getChildrenUnmodifiable();
        boolean notActive = false;

        for (Node node: nodes) {
            ObservableList<String> classes = node.getStyleClass();

            if (node == button) {
                if (!classes.toString().contains("active")) {
                    classes.add("active");
                    notActive = true;
                }
            } else {
                classes.remove("active");
            }
        }

        return notActive;
    }
}
