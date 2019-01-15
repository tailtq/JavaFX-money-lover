package com.moneylover.Modules.Category.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

public class Category extends BaseModel {
    private int typeId;

    private String moneyType;

    private String name;

    private String icon;

    public Category() {}

    public Category(int typeId, String moneyType, String name, String icon) {
        this.typeId = typeId;
        this.moneyType = moneyType;
        this.name = name;
        this.icon = icon;
    }

    public static String getTable() {
        return "categories";
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
