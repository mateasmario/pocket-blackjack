package com.hacklicious.pocketblackjack.service;

import com.hacklicious.pocketblackjack.comparator.CardComparator;
import com.hacklicious.pocketblackjack.entity.Card;
import com.hacklicious.pocketblackjack.entity.Player;
import com.hacklicious.pocketblackjack.enumeration.CardValue;

import java.util.ArrayList;
import java.util.List;

public class PlayerService {
    private List<Player> playerList;

    public void initializePlayerList(int count) {
        playerList = new ArrayList<>();

        for (int i = 0; i<count; i++) {
            playerList.add(new Player("Player " + i));
        }
    }

    public Player getPlayer(int i) {
        return playerList.get(i);
    }

    public void addCard(int playerId, Card card) {
        playerList.get(playerId).getLocalDeck().add(card);
    }

    public boolean isWin(int playerId) {
        Player player = playerList.get(playerId);
        List<Card> localDeck = player.getLocalDeck();
        int deckValue = getDeckValue(player.getLocalDeck());

        return deckValue == 21 || (deckValue < 21 && localDeck.size() == 7);
    }

    public boolean isLose(int playerId) {
        Player player = playerList.get(playerId);
        List<Card> localDeck = player.getLocalDeck();
        int deckValue = getDeckValue(localDeck);

        return deckValue > 21;
    }

    public List<Player> identifyWinners() {
        List<Player> winnerList = new ArrayList<>();
        int maxValue = -1;

        for (Player player : playerList) {
            int deckValue = getDeckValue(player.getLocalDeck());
            if (deckValue > maxValue) {
                maxValue = deckValue;
            }
        }

        for(Player player: playerList) {
            int deckValue = getDeckValue(player.getLocalDeck());
            if (deckValue == maxValue) {
                winnerList.add(player);
            }
        }

        return winnerList;
    }

    public int getDeckValue(List<Card> deck) {
        int deckValue = 0;
        deck.sort(new CardComparator());

        for(Card card : deck) {
            if (card.getCardValue() != CardValue.A) {
                deckValue += card.getCardValue().getValue();
            }
            else {
                if (deckValue + 11 <= 21) {
                    deckValue += 11;
                }
                else {
                    deckValue += 1;
                }
            }
        }

        return deckValue;
    }
}
