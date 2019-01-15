package com.moneylover.Modules.Time.Entities;

public class Day {
    private String dayOfWeek;

    private int dayOfMonth;

    private String month;

    public Day(String dayOfWeek, int dayOfMonth, String month) {
        this.dayOfWeek = dayOfWeek;
        this.dayOfMonth = dayOfMonth;
        this.month = month;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public String getMonth() {
        return month;
    }
}
