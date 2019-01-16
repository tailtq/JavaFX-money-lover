package com.moneylover.Modules.Wallet.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

public class Wallet extends BaseModel {
    private int currencyId;

    private String name;

    private float inflow;

    private float outflow;

    private String moneySymbol;

    public Wallet() {}

    public Wallet(int currencyId, String name, float amount) {
        this.currencyId = currencyId;
        this.name = name;
        this.inflow = 0;
        this.outflow = 0;
        if (amount > 0) {
            this.inflow = amount;
        } else if (amount < 0) {
            this.outflow = amount;
        }
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

    public float getInflow() {
        return inflow;
    }

    public void setInflow(float inflow) {
        this.inflow = inflow;
    }

    public float getOutflow() {
        return outflow;
    }

    public void setOutflow(float outflow) {
        this.outflow = outflow;
    }

    public String getMoneySymbol() {
        return moneySymbol;
    }

    public void setMoneySymbol(String moneySymbol) {
        this.moneySymbol = moneySymbol;
    }
}
