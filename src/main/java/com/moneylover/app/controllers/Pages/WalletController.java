package com.moneylover.app.controllers.Pages;

import com.moneylover.app.controllers.BaseViewController;
import com.moneylover.app.controllers.Contracts.LoaderInterface;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class WalletController extends BaseViewController implements LoaderInterface {
    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/wallet.fxml"));
        fxmlLoader.setController(this);
        VBox vBox = fxmlLoader.load();

        return vBox;
    }

    @FXML void createWallet(Event e) {

    }

    @FXML
    public void editWallet(Event e) {

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
