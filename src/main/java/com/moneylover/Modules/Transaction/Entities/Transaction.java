package com.moneylover.Modules.Transaction.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

import java.util.Date;

public class Transaction extends BaseModel {
    private int walletId;

    private int timeId;

    private int typeId;

    private int categoryId;

    private int subCategoryId;

    private Date transactedAt;

    private float amount;

    private String location;

    private String note;

    private String image;

    private byte isReported;

    public static String getTable() {
        return "transactions";
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
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

    public Date getTransactedAt() {
        return transactedAt;
    }

    public void setTransactedAt(Date transactedAt) {
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
}
