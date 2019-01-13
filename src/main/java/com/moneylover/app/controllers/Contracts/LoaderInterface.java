package com.moneylover.app.controllers.Contracts;

import com.moneylover.Modules.User.Entities.User;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public interface LoaderInterface {
    VBox loadView() throws IOException;

    void setUser(User user) throws IOException;

    void setWallets(ObservableList<Wallet> wallets) throws IOException;
}
