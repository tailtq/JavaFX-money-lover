package com.moneylover.Modules.Type.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

public class Type extends BaseModel {
    private String moneyType;

    private String name;

    public Type() {}

    public Type(String moneyType, String name) {
        this.moneyType = moneyType;
        this.name = name;
    }

    public static String getTable() {
        return "types";
    }

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
