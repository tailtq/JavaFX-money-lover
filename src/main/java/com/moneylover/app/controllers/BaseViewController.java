package com.moneylover.app.controllers;

import com.moneylover.app.controllers.Contracts.DialogInterface;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BaseViewController implements DialogInterface {
    protected void createScreen(Parent parent, String title, int v, int v1) {
        Stage stage = new Stage();
        stage.setScene(new Scene(parent, v, v1));
        stage.setMinWidth(v);
        stage.setMinHeight(v1);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
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

    protected void activeTab(Event e, TabPane tabPane) {
        Button button = (Button) e.getSource();
        boolean notActive = this.activeButton(button);

        if (notActive) {
            int value = Integer.parseInt(button.getUserData().toString());
            tabPane.getSelectionModel().select(value);
        }
    }

    protected void setFieldsNull(TextField ... textFields) {
        for (TextField field: textFields) {
            field.setText(null);
        }
    }
}
