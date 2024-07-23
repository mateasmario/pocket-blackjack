package com.hacklicious.pocketblackjack.service;

import com.hacklicious.pocketblackjack.entity.Card;
import com.hacklicious.pocketblackjack.enumeration.CardShape;
import com.hacklicious.pocketblackjack.enumeration.CardValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardService {
    private List<Card> deck;

    public void initializeGlobalDeck() {
        deck = new ArrayList<>();

        for (CardShape cardShape : CardShape.values()) {
            for (CardValue cardValue : CardValue.values()) {
                deck.add(new Card(cardShape, cardValue));
            }
        }
    }

    public Card popRandomCard() {
        Random random = new Random();
        Card randomCard =  deck.get(random.nextInt(deck.size()));
        deck.remove(randomCard);
        return randomCard;
    }

    public String mapCardToString(Card card) {
        String cardString;

        if (card.getCardValue().equals(CardValue.A) || card.getCardValue().equals(CardValue.J) || card.getCardValue().equals(CardValue.Q) || card.getCardValue().equals(CardValue.K)) {
            cardString = card.getCardValue().toString();
        }
        else {
            cardString = String.valueOf(card.getCardValue().getValue());
        }

        switch(card.getCardShape()) {
            case CLUBS -> cardString += " ♣";
            case HEARTS -> cardString += " ♥";
            case SPADES -> cardString += " ♠";
            case DIAMONDS -> cardString += " ♦";
        }

        return cardString;
    }
}
