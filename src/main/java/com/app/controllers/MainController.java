package main.java.com.app.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import main.java.com.app.controllers.Pages.TransactionController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private BooleanProperty changeScene = new SimpleBooleanProperty(false);

    private VBox mainView;

    public BooleanProperty getChangeScene() {
        return changeScene;
    }

    public void setChangeScene(boolean changeScene) {
        this.changeScene.setValue(changeScene);
    }

    public VBox getMainView() {
        return this.mainView;
    }

    @FXML
    public void pressTransaction(Event e) throws IOException {
        Node button = (Node) e.getSource();
        TransactionController transactionController = new TransactionController();
        boolean result = this.handleSidebarButtonClass(button);

        if (result) {
            this.mainView = transactionController.loadView();
        }
    }

    @FXML
    public void pressReport(Event e) {
        Node button = (Node) e.getSource();
        boolean result = this.handleSidebarButtonClass(button);
    }

    @FXML
    public void pressBudget(Event e) {
        Node button = (Node) e.getSource();
        boolean result = this.handleSidebarButtonClass(button);
    }

    private boolean handleSidebarButtonClass(Node button) {
        ObservableList<Node> nodes = button.getParent().getChildrenUnmodifiable();
        boolean newScene = false;

        for (Node node: nodes) {
            ObservableList<String> classes = node.getStyleClass();

            if (node == button) {
                if (!classes.toString().contains("active")) {
                    classes.add("active");
                    this.setChangeScene(true);
                    newScene = true;
                }
            } else {
                classes.remove("active");
            }
        }

        return newScene;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            TransactionController transactionController = new TransactionController();
            this.mainView = transactionController.loadView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
