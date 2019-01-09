package com.moneylover.Modules.Currency.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

public class Currency extends BaseModel {
    private String name;

    private String code;

    private String symbol;

    private String image;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
