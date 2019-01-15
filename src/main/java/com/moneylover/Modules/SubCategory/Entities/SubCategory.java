package com.moneylover.Modules.SubCategory.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

public class SubCategory extends BaseModel {
    private int typeId;

    private int categoryId;

    private String moneyType;

    private String name;

    private String icon;

    public SubCategory() {}

    public SubCategory(int typeId, int categoryId, String moneyType, String name, String icon) {
        this.typeId = typeId;
        this.categoryId = categoryId;
        this.moneyType = moneyType;
        this.name = name;
        this.icon = icon;
    }

    public static String getTable() {
        return "sub_categories";
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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
