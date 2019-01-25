package com.moneylover.Modules.Wallet.Controllers;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Wallet.Entities.UserWallet;
import com.moneylover.Modules.Wallet.Entities.Wallet;
import com.moneylover.Modules.Wallet.Services.WalletService;

import java.sql.SQLException;
import java.util.ArrayList;

public class WalletController {
    private WalletService service;

    public WalletController() throws SQLException, ClassNotFoundException {
        service = new WalletService();
    }

    public ArrayList<Wallet> list() throws SQLException {
        ArrayList<Wallet> wallets = this.service.list();

        return wallets;
    }

    public ArrayList<Wallet> list(int userId) throws SQLException {
        ArrayList<Wallet> wallets = this.service.list(userId);

        return wallets;
    }

    public Wallet getDetail(int id) throws SQLException, NotFoundException {
        Wallet wallet = this.service.getDetail(id);

        return wallet;
    }

    public Wallet create(Wallet wallet) throws SQLException, NotFoundException {
        Wallet newWallet = this.service.create(wallet);

        return newWallet;
    }

    public boolean attachUsers(ArrayList<UserWallet> userWallets) throws SQLException {
        boolean result = this.service.attachUsers(userWallets);

        return result;
    }

    public boolean update(Wallet wallet, int id) throws SQLException, NotFoundException {
        boolean result = this.service.update(wallet, id);

        return result;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.delete(id);
    }
}
