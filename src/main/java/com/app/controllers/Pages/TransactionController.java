package main.java.com.app.controllers.Pages;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import main.java.com.app.controllers.Contracts.LoaderInterface;

import java.io.IOException;

public class TransactionController implements LoaderInterface {

    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../../../../resources/views/pages/transaction.fxml"));

        return fxmlLoader.load();
    }
}
