package com.hacklicious.pocketblackjack.entity;

import com.hacklicious.pocketblackjack.enumeration.CardShape;
import com.hacklicious.pocketblackjack.enumeration.CardValue;

public class Card {
    private CardShape cardShape;
    private CardValue cardValue;

    public Card(CardShape cardShape, CardValue cardValue) {
        this.cardShape = cardShape;
        this.cardValue = cardValue;
    }

    public CardShape getShape() {
        return cardShape;
    }

    public void setShape(CardShape cardShape) {
        this.cardShape = cardShape;
    }

    public CardShape getCardShape() {
        return cardShape;
    }

    public void setCardShape(CardShape cardShape) {
        this.cardShape = cardShape;
    }

    public CardValue getCardValue() {
        return cardValue;
    }

    public void setCardValue(CardValue cardValue) {
        this.cardValue = cardValue;
    }
}
