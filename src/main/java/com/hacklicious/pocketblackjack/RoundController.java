package com.hacklicious.pocketblackjack;

import com.hacklicious.pocketblackjack.entity.Card;
import com.hacklicious.pocketblackjack.entity.Player;
import com.hacklicious.pocketblackjack.enumeration.CardShape;
import com.hacklicious.pocketblackjack.enumeration.Decision;
import com.hacklicious.pocketblackjack.service.CardService;
import com.hacklicious.pocketblackjack.service.DecisionService;
import com.hacklicious.pocketblackjack.service.PlayerService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;

public class RoundController {
    private CardService cardService = new CardService();
    private PlayerService playerService = new PlayerService();
    private DecisionService decisionService = new DecisionService();

    private static final int PLAYER_COUNT = 5;
    private static final double RISK_DECISION = 0.75;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vbox;

    @FXML
    private Button hitButton;

    @FXML
    private Button standButton;

    @FXML
    private Button startButton;

    @FXML
    protected void initialize() {
        scrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            vbox.setPrefWidth(newValue.getWidth());
        });

        toggleInterraction(false);

        for(int i = 0; i<PLAYER_COUNT; i++) {
            VBox playerBox = new VBox();

            HBox playerTitleHbox = new HBox();
            Label playerTitleLabel = new Label();

            String playerName;
            switch(i) {
                case PLAYER_COUNT - 2:
                    playerName = "You";
                    break;
                case PLAYER_COUNT-1:
                    playerName = "Dealer";
                    break;
                default:
                    playerName = "Player " + i;
            }
            playerTitleLabel.setText(playerName);
            playerTitleHbox.getChildren().add(playerTitleLabel);

            HBox playerCardsHbox = new HBox();
            playerCardsHbox.setId(String.valueOf(i));
            playerCardsHbox.setSpacing(10);

            playerBox.getChildren().add(0, playerTitleHbox);
            playerBox.getChildren().add(1, playerCardsHbox);

            vbox.getChildren().add(playerBox);
        }
    }

    @FXML
    protected void onStartButtonClick() {
        toggleInterraction(false);
        startButton.setDisable(true);

        cardService.initializeGlobalDeck();
        playerService.initializePlayerList(PLAYER_COUNT);

        // Bot logic
        for (int playerId = 0; playerId < PLAYER_COUNT - 2; playerId++) {
            executeBotLogic(playerId);
        }

        // Player logic
        toggleInterraction(true);
    }


    @FXML
    protected void onHitButtonClick() {
        toggleInterraction(false);

        Card randomCard = cardService.popRandomCard();
        playerService.addCard(PLAYER_COUNT - 2, randomCard);
        addCardToPlayer(PLAYER_COUNT - 2, randomCard);

        if (playerService.isWin(PLAYER_COUNT - 2)) {
            // Player won. Show win screen
        } else if (playerService.isLose(PLAYER_COUNT - 2)) {
            // Player lost. Show lose screen
            startButton.setDisable(false);
        } else {
            toggleInterraction(true);
        }
    }

    @FXML
    protected void onStandButtonClick() {
        toggleInterraction(false);

        // Execute dealer logic
        executeBotLogic(PLAYER_COUNT - 1);

        List<Player> winnerList = playerService.identifyWinners();

        if (winnerList.size() == 1) {
            // User is the winner
            if (winnerList.get(0).equals(playerService.getPlayer(PLAYER_COUNT - 2))) {
                // Display win message
            } else {
                // Display lose message for user (someone else won)
            }
        } else {
            if (winnerList.contains(playerService.getPlayer(PLAYER_COUNT - 2))) {
                // Display win message
            } else {
                // Display lose message for user (someone else won)
            }
        }

    }

    private void executeBotLogic(int playerId) {
        boolean satisfied;

        do {
            satisfied = false;

            if (playerService.getDeckValue(playerService.getPlayer(playerId).getLocalDeck()) < 17) {
                Card randomCard = cardService.popRandomCard();
                playerService.addCard(playerId, randomCard);

                addCardToPlayer(playerId, randomCard);

                satisfied = true;
            } else {
                if (decisionService.generateRiskDecision(RISK_DECISION).equals(Decision.YES)) {
                    Card randomCard = cardService.popRandomCard();
                    playerService.addCard(playerId, randomCard);

                    satisfied = false;
                } else {
                    // Bot stands
                }
            }
        } while(satisfied);
    }

    private void addCardToPlayer(int playerId, Card card) {
        HBox hbox = (HBox)(vbox.lookup("#" + playerId));
        Label cardLabel = new Label();
        cardLabel.setText(cardService.mapCardToString(card));

        if (card.getCardShape().equals(CardShape.DIAMONDS) || card.getCardShape().equals(CardShape.HEARTS)) {
            cardLabel.setTextFill(Color.color(1, 0, 0));
        }
        else if (card.getCardShape().equals(CardShape.CLUBS) || card.getCardShape().equals(CardShape.SPADES)) {
            cardLabel.setTextFill(Color.color(0, 0, 0));
        }

        hbox.getChildren().add(playerService.getPlayer(playerId).getLocalDeck().size()-1, cardLabel);
    }

    private void toggleInterraction(boolean flag) {
        hitButton.setDisable(!flag);
        standButton.setDisable(!flag);
    }
}