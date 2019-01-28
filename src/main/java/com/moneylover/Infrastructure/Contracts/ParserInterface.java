package com.moneylover.Infrastructure.Contracts;

public interface ParserInterface {
    default String toMoney(float amount) {
        return String.format("%.1f", amount);
    }

    default String toMoneyString(float amount) {
        return (amount > 0 ? "+" : "") + this.toMoney(amount);
    }
}
