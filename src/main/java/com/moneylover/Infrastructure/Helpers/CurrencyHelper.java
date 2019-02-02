package com.moneylover.Infrastructure.Helpers;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyHelper {
    public static String toMoney(float amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

        return nf.format(amount);
    }

    public static String parseToCurrency(String text) {
        if (text.equals("")) {
            return "";
        }

        String currentText = text;
        text = text.replaceAll("[^\\d.]", "");

        if (text.isEmpty()) {
            return text;
        }

        String[] amountTextArray = text.split("\\.");
        int quantity = amountTextArray.length;

        if (quantity == 1 && currentText.charAt(currentText.length() - 1) == '.') {
            text = currentText;
        } else {
            if (quantity >= 2) {
                text = amountTextArray[0] + "." + amountTextArray[1];
            }

            NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
            text = nf.format(Double.valueOf(text));
        }

        return text;
    }

    public static String toMoneyString(float amount, String moneySymbol) {
        return (amount > 0 ? "+" : "") + CurrencyHelper.toMoney(amount) + moneySymbol;
    }
}
