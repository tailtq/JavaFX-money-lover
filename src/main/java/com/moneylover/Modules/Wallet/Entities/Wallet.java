package com.moneylover.Modules.Wallet.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

public class Wallet extends BaseModel {
    private int currencyId;

    private String name;

    private float amount;

    private String moneySymbol;

    public Wallet() {}

    public Wallet(int currencyId, String name, float amount) {
        this.currencyId = currencyId;
        this.name = name;
        this.amount = amount;
    }

    public static String getTable() {
        return "wallets";
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getMoneySymbol() {
        return moneySymbol;
    }

    public void setMoneySymbol(String moneySymbol) {
        this.moneySymbol = moneySymbol;
    }
}
