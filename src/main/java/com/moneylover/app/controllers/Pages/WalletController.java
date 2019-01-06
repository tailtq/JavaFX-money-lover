package com.moneylover.app.controllers.Pages;

import com.moneylover.app.controllers.BaseViewController;
import com.moneylover.app.controllers.Contracts.LoaderInterface;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class WalletController extends BaseViewController implements LoaderInterface {
    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/wallet.fxml"));
        fxmlLoader.setController(this);
        VBox vBox = fxmlLoader.load();

        return vBox;
    }

    @FXML
    public void createWallet(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/wallet-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(parent, 500, 115));
        stage.setTitle("Create Wallet");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    public void editWallet(Event e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/wallet-create.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(parent, 500, 115));
        stage.setTitle("Edit Wallet");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    public void chooseCurrency() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/choose-currency.fxml"));
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(parent, 400, 300));
        stage.setTitle("Choose Currency");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    public void deleteWallet(Event e) {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            // Delete Wallet
            System.out.println("Yes");
        }
    }
}
