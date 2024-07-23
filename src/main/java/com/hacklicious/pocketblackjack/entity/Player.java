package com.hacklicious.pocketblackjack.entity;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> localDeck;

    public Player(String name) {
        this.name = name;
        this.localDeck = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getLocalDeck() {
        return localDeck;
    }

    public void setLocalDeck(List<Card> localDeck) {
        this.localDeck = localDeck;
    }
}
