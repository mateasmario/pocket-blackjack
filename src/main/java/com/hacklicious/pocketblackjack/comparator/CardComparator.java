package com.hacklicious.pocketblackjack.comparator;

import com.hacklicious.pocketblackjack.entity.Card;

import java.util.Comparator;

public class CardComparator implements Comparator<Card> {
    @Override
    public int compare(Card o1, Card o2) {
        return o1.getCardValue().getValue() - o2.getCardValue().getValue();
    }
}
