package com.moneylover.Modules.Currency.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

public class Currency extends BaseModel {
    private String name;

    private String code;

    private String symbol;

    private String icon;

    public static String getTable() {
        return "currencies";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
