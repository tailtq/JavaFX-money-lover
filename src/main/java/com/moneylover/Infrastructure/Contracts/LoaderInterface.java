package com.moneylover.Infrastructure.Contracts;

import com.moneylover.Modules.User.Entities.User;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;

public interface LoaderInterface {
    void setChangeWallet(IntegerProperty changeWallet);

    void setWallets(ObservableList<Wallet> wallets) throws IOException, SQLException;
}
