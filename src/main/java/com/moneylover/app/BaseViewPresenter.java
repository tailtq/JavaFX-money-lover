package com.moneylover.app;

import com.moneylover.Infrastructure.Contracts.DialogInterface;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.*;

abstract public class BaseViewPresenter implements DialogInterface {
    public boolean activeButton(Node button) {
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
            field.setText("");
        }
    }
}
