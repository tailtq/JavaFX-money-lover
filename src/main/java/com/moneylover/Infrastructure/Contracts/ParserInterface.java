package com.moneylover.Infrastructure.Contracts;

import java.text.NumberFormat;
import java.util.Locale;

public interface ParserInterface {
    default String toMoney(float amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

        return nf.format(amount);
    }

    default String toMoneyString(float amount) {
        return (amount > 0 ? "+" : "") + this.toMoney(amount);
    }

    default String toMoneyString(float amount, String moneySymbol) {
        return (amount > 0 ? "+" : "") + this.toMoney(amount) + moneySymbol;
    }
}
