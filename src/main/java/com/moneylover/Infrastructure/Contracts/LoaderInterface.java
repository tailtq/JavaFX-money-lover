package com.moneylover.Infrastructure.Contracts;

import com.moneylover.Modules.Wallet.Entities.Wallet;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;

public interface LoaderInterface {
    void loadPresenter() throws SQLException, ClassNotFoundException;

    void setWalletIndex(IntegerProperty walletIndex);

    void setWallets(ObservableList<Wallet> wallets) throws IOException, SQLException, InterruptedException;
}
