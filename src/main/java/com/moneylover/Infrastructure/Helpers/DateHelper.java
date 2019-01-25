package com.moneylover.Infrastructure.Helpers;

import java.time.LocalDate;

public class DateHelper {
    public static boolean isSameDay(LocalDate startDate, LocalDate endDate) {
        return startDate.getDayOfMonth() == endDate.getDayOfMonth()
                && DateHelper.isSameMonth(startDate, endDate)
                && DateHelper.isSameYear(startDate, endDate);
    }

    public static boolean isLaterThan(LocalDate comparedDate, LocalDate date) {
        if (date.getYear() > comparedDate.getYear()) {
            return true;
        }

        if (DateHelper.isSameYear(date, comparedDate)
                && date.getMonthValue() > comparedDate.getMonthValue()) {
            return true;
        }

        // TODO: Equal
        if (DateHelper.isSameYear(date, comparedDate)
                && DateHelper.isSameMonth(date, comparedDate)
                && date.getDayOfMonth() >= comparedDate.getDayOfMonth()) {
            return true;
        }

        return false;
    }

    public static boolean isSameMonth(LocalDate startDate, LocalDate endDate) {
        return startDate.getMonthValue() == endDate.getMonthValue() && DateHelper.isSameYear(startDate, endDate);
    }

    public static boolean isSameYear(LocalDate startDate, LocalDate endDate) {
        return startDate.getYear() == endDate.getYear();
    }
}
