package com.moneylover.Modules.Budget.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

import java.time.LocalDate;

public class Budget extends BaseModel {
    private int walletId;

    private int budgetableId;

    private String budgetableType;

    private LocalDate startedAt;

    private LocalDate endedAt;

    private float amount;

    private float spentAmount;

    private String categoryIcon;

    private String categoryName;

    public static String getTable() {
        return "budgets";
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public int getBudgetableId() {
        return budgetableId;
    }

    public void setBudgetableId(int budgetableId) {
        this.budgetableId = budgetableId;
    }

    public String getBudgetableType() {
        return budgetableType;
    }

    public void setBudgetableType(String budgetableType) {
        this.budgetableType = budgetableType;
    }

    public LocalDate getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDate startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDate getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDate endedAt) {
        this.endedAt = endedAt;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(float spentAmount) {
        this.spentAmount = spentAmount;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
