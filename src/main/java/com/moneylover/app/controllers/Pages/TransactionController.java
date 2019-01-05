package com.moneylover.app.controllers.Pages;

import com.moneylover.app.controllers.BaseViewController;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.moneylover.app.controllers.Contracts.LoaderInterface;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class TransactionController extends BaseViewController implements LoaderInterface {

    @FXML
    private Button leftTime, middleTime, rightTime;

    @FXML
    private VBox transactionContent, transactionTimes;

    @FXML
    private Label inflow;

    @FXML
    private Button createButton;

    @FXML
    private TextField amount;

    @FXML
    private DatePicker transactedAt;

    @FXML
    private TreeView categoriesView;

    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/transaction.fxml"));
        fxmlLoader.setController(this);
        VBox vBox = fxmlLoader.load();

        return vBox;
    }

    @FXML
    public void changeTime(Event e) {
        Node button = (Node) e.getSource();
        if (button == leftTime) {
            middleTime.setText("Hello");
        } else if (button == rightTime) {
            middleTime.setText("Good morning");
        }
    }

    @FXML
    public void showCreateTransactionDialog(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction-create.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        this.transactedAt.setValue(LocalDate.now());

        Scene scene = new Scene(parent, 500, 300);
        Stage stage = new Stage();
        stage.setTitle("Add Transaction");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    public void editTransaction(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction-create.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 500, 300);
        Stage stage = new Stage();
        stage.setTitle("Edit Transaction");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    public void deleteTransaction(Event e) {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            System.out.println("Yes");
        }
    }

    @FXML
    public void showCategoryDialog(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/choose-category.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 300, 200);
        Stage stage = new Stage();
        stage.setTitle("Choose Category");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    public void showFriendDialog(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/choose-friends.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 300, 200);
        Stage stage = new Stage();
        stage.setTitle("Choose Friend");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    public void changeType(Event e) {
        Node button = (Node) e.getSource();
        ObservableList<Node> nodes = button.getParent().getChildrenUnmodifiable();

        for (Node node: nodes) {
            ObservableList<String> classes = node.getStyleClass();

            if (node == button) {
                if (!classes.toString().contains("active")) {
                    classes.add("active");
                }
            } else {
                classes.remove("active");
            }
        }
        // Change categoriesView
    }
}
