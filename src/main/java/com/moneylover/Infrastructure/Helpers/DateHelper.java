package com.moneylover.Infrastructure.Helpers;

import com.moneylover.Infrastructure.Constants.CommonConstants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        if (DateHelper.isSameMonth(date, comparedDate)
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

    public static DateTimeFormatter getFormat() {
        return DateTimeFormatter.ofPattern("MM/dd/YYYY");
    }

    public static String getDateRange(LocalDate startDate, LocalDate endDate) {
        if (DateHelper.isSameMonth(startDate, endDate)) {
            return CommonConstants.DAY_RANGE;
        } else if (DateHelper.isSameYear(startDate, endDate)) {
            return CommonConstants.MONTH_RANGE;
        } else {
            return CommonConstants.YEAR_RANGE;
        }
    }
}
