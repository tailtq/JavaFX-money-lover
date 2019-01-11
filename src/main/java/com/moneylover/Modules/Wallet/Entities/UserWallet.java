package com.moneylover.Modules.Wallet.Entities;

public class UserWallet {
    private int userId;

    private int walletId;

    public UserWallet(int userId, int walletId) {
        this.userId = userId;
        this.walletId = walletId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }
}
