package com.moneylover.app.controllers.Pages;

import com.moneylover.app.controllers.Contracts.LoaderInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class UserController implements LoaderInterface {

    @Override
    public VBox loadView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/user/user.fxml"));
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();

        return parent;
    }
}
