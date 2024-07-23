package com.hacklicious.pocketblackjack.enumeration;

public enum CardValue {
    A(-1), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), J(10), Q(10), K(10);

    private int value;

    private CardValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
