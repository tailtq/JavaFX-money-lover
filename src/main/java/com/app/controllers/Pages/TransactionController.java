package main.java.com.app.controllers.Pages;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import main.java.com.app.controllers.Contracts.LoaderInterface;

import java.io.IOException;

public class TransactionController implements LoaderInterface {

    @FXML
    Button leftTime, middleTime, rightTime;

    @FXML
    VBox transactionContent;

    @FXML
    Label inflow;

    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../../../../resources/views/pages/transaction.fxml"));

        return fxmlLoader.load();
    }

    @FXML
    public void changeTime(Event e) {
        Node button = (Node) e.getSource();
//        if (button == leftTime) {
//            middleTime.setText("Hello");
//        } else if (button == rightTime) {
//            middleTime.setText("Good morning");
//        }
    }


}
