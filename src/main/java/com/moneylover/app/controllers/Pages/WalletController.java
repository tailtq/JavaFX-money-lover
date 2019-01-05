package com.moneylover.app.controllers.Pages;

import com.moneylover.app.controllers.Contracts.LoaderInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class WalletController implements LoaderInterface {
    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/wallet.fxml"));

        return fxmlLoader.load();
    }
}
