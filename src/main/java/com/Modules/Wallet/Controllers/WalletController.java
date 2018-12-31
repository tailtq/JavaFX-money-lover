package main.java.com.Modules.Wallet.Controllers;

import main.java.com.Infrastructure.Exceptions.NotFoundException;
import main.java.com.Modules.Wallet.Entities.Wallet;
import main.java.com.Modules.Wallet.Services.WalletService;

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

    public Wallet getDetail(int id) throws SQLException, NotFoundException {
        Wallet wallet = this.service.getDetail(id);

        return wallet;
    }

    public Wallet create(Wallet wallet) throws SQLException, NotFoundException {
        Wallet newWallet = this.service.create(wallet);

        return newWallet;
    }

    public Wallet update(Wallet wallet, int id) throws SQLException, NotFoundException {
        Wallet updatedWallet = this.service.update(wallet, id);

        return updatedWallet;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }
}
