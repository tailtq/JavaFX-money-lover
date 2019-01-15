package com.moneylover.Modules.Time.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

import java.util.Date;

public class Time extends BaseModel {
    private int month;

    private int year;

    public Time() {}

    public Time(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public Time(int id, int month, int year, Date createdAt) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.createdAt = createdAt;
    }

    public static String getTable() {
        return "times";
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
