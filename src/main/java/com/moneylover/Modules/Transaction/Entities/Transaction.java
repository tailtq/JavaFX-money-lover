package com.moneylover.Modules.Transaction.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

import java.time.LocalDate;

public class Transaction extends BaseModel {
    private int walletId;

    private int typeId;

    private int categoryId;

    private int subCategoryId;

    private LocalDate transactedAt;

    private float amount;

    private String location;

    private String note;

    private String image;

    private byte isReported;

    private String categoryName;

    private String categoryIcon;

    private String categoryMoneyType;

    private String subCategoryName;

    private String subCategoryIcon;

    public static String getTable() {
        return "transactions";
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
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

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public LocalDate getTransactedAt() {
        return transactedAt;
    }

    public void setTransactedAt(LocalDate transactedAt) {
        this.transactedAt = transactedAt;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public byte getIsReported() {
        return isReported;
    }

    public void setIsReported(byte isReported) {
        this.isReported = isReported;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryMoneyType() {
        return categoryMoneyType;
    }

    public void setCategoryMoneyType(String categoryMoneyType) {
        this.categoryMoneyType = categoryMoneyType;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryIcon() {
        return subCategoryIcon;
    }

    public void setSubCategoryIcon(String subCategoryIcon) {
        this.subCategoryIcon = subCategoryIcon;
    }
}
