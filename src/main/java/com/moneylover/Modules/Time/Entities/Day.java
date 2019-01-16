package com.moneylover.Modules.Time.Entities;

public class Day {
    private int dayOfMonth;

    private String dayOfWeek;

    private int monthNumber;

    private String month;

    private int year;

    private String symbol;

    public Day(int monthNumber, int year) {
        this.monthNumber = monthNumber;
        this.year = year;
    }

    public Day(int dayOfMonth, String dayOfWeek, String month, String symbol) {
        this.dayOfMonth = dayOfMonth;
        this.dayOfWeek = dayOfWeek;
        this.month = month;
        this.symbol = symbol;
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

    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getSymbol() {
        return symbol;
    }
}
