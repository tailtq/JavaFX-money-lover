package com.moneylover.Modules.Wallet.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

public class Wallet extends BaseModel {
    private int id;

    private int currencyId;

    private String name;

    private float inflow;

    private float outflow;

    public static String getTable() {
        return "wallets";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
