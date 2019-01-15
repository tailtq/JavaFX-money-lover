package com.moneylover.app.controllers.Pages.Transaction;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Time.Controllers.TimeController;
import com.moneylover.Modules.Time.Entities.Day;
import com.moneylover.Modules.Time.Entities.Time;
import com.moneylover.Modules.Transaction.Entities.Transaction;
import com.moneylover.app.controllers.Contracts.UseCategoryInterface;
import com.moneylover.app.controllers.PageController;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransactionController extends PageController implements UseCategoryInterface {
    private com.moneylover.Modules.Transaction.Controllers.TransactionController transactionController;

    private TimeController timeController;

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private Time time;

    public TransactionController(BooleanProperty changeWallet) throws SQLException, ClassNotFoundException {
        this.changeWallet = changeWallet;
        this.timeController = new TimeController();
        this.transactionController = new com.moneylover.Modules.Transaction.Controllers.TransactionController();
//        this.transactions =
    }

    private void getTransactionsByMonth(int month) throws SQLException {
        this.transactions.clear();
        this.transactions.addAll(this.transactionController.list(month));
    }

    /*========================== Draw ==========================*/
    @FXML
    private TabPane categoriesTabPane;

    @FXML
    private Button leftTime, middleTime, rightTime;

    @FXML
    private ListView transactionDays;

//    @FXML
//    private VBox transactionContent, transactionTimes;

//    @FXML
//    private Label inflow;

    @FXML
    private TextField amount;

    @FXML
    private DatePicker transactedAt;

//    @FXML
//    private TreeView categoriesView;

    @Override
    public VBox loadView() throws IOException, NotFoundException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/transaction/transaction.fxml"));
        fxmlLoader.setController(this);
        VBox vBox = fxmlLoader.load();

        LocalDate currentDate = LocalDate.now();
        this.time = this.timeController.getDetail(
                currentDate.getMonth().getValue(),
                currentDate.getYear()
        );
        ObservableList<Pair<Day, ObservableList<Transaction>>> values = FXCollections.observableArrayList();
        values.add(new Pair<>(
                new Day("Tuesday", 10, "January"),
                FXCollections.observableArrayList(new Transaction(), new Transaction()))
        );
        values.add(new Pair<>(
                new Day("Tuesday", 10, "January"),
                FXCollections.observableArrayList(new Transaction(), new Transaction()))
        );
        values.add(new Pair<>(
                new Day("Tuesday", 10, "January"),
                FXCollections.observableArrayList(new Transaction()))
        );

        this.transactionDays.setItems(values);
        this.transactionDays.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                try {
                    return new TransactionDate();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

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
    public void createTransaction(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction-create.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        this.transactedAt.setValue(LocalDate.now());

        this.createScreen(parent, "Add Transaction", 500, 230);
    }

    @FXML
    public void editTransaction(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/transaction-create.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        this.createScreen(parent, "Edit Transaction", 500, 230);
    }

    @FXML
    public void deleteTransaction(Event e) {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            System.out.println("Yes");
        }
    }

    @FXML
    private void chooseCategory(Event e) throws IOException {
        this.showCategoryDialog(e);
    }

    @FXML
    public void showFriendDialog(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/choose-friends.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        this.createScreen(parent, "Choose Friend", 300, 200);
    }

    @FXML
    public void changeTab(Event e) {
        this.activeTab(e, this.categoriesTabPane);
    }
}
