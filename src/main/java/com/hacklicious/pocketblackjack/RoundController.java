package com.hacklicious.pocketblackjack;

import com.hacklicious.pocketblackjack.constant.MessageConstants;
import com.hacklicious.pocketblackjack.constant.StyleConstants;
import com.hacklicious.pocketblackjack.entity.Card;
import com.hacklicious.pocketblackjack.entity.Player;
import com.hacklicious.pocketblackjack.enumeration.CardShape;
import com.hacklicious.pocketblackjack.enumeration.Decision;
import com.hacklicious.pocketblackjack.service.CardService;
import com.hacklicious.pocketblackjack.service.DecisionService;
import com.hacklicious.pocketblackjack.service.PlayerService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;

public class RoundController {
    private final CardService cardService = new CardService();
    private final PlayerService playerService = new PlayerService();
    private final DecisionService decisionService = new DecisionService();

    private static final int PLAYER_COUNT = 3;
    private static final int PLAYER_DEALER = PLAYER_COUNT - 1;
    private static final int PLAYER_USER = PLAYER_COUNT - 2;

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
    private Label secondaryText;

    @FXML
    private Label primaryText;

    @FXML
    protected void initialize() {
        scrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> vbox.setPrefWidth(newValue.getWidth()));

        toggleInteraction(false);

        for (int playerId = 0; playerId < PLAYER_COUNT; playerId++) {
            VBox playerBox = new VBox();

            HBox playerTitleHbox = new HBox();
            Label playerTitleLabel = new Label();
            playerTitleLabel.setStyle("-fx-font-weight: bold;");

            playerTitleLabel.setText(playerService.mapPlayerIdToString(PLAYER_COUNT, playerId));
            playerTitleHbox.getChildren().add(playerTitleLabel);

            HBox playerCardsHbox = new HBox();
            playerCardsHbox.setId("cards_" + playerId);
            playerCardsHbox.setSpacing(10);

            Label label = new Label();
            label.setText(MessageConstants.NO_CARDS);
            playerCardsHbox.getChildren().add(0, label);

            playerBox.getChildren().add(0, playerTitleHbox);
            playerBox.getChildren().add(1, playerCardsHbox);
            playerBox.setId("box_" + playerId);

            playerBox.setStyle(StyleConstants.PLAYER_BOX);

            vbox.getChildren().add(playerBox);
        }
    }

    @FXML
    protected void onStartButtonClick() {
        if (playerService.getPlayerList() != null) {
            for (int playerId = 0; playerId < PLAYER_COUNT; playerId++) {
                vbox.getChildren().remove(0);
            }

            initialize();
        }

        startButton.setDisable(true);
        toggleInteraction(false);

        cardService.initializeGlobalDeck();
        playerService.initializePlayerList(PLAYER_COUNT);

        final int[] playerId = {0};
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            if (isUser(playerId[0])) {
                timeline.stop();
            } else if (!isFirstBot(playerId[0])) {
                if (playerService.getPlayer(0).isReady()) {
                    executeBotLogic(playerId[0]);
                    playerId[0]++;
                } else {
                    // Player not yet ready (for debugging)
                }
            } else {
                executeBotLogic(playerId[0]);
                playerId[0]++;
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    @FXML
    protected void onHitButtonClick() {
        toggleInteraction(false);

        Card randomCard = cardService.popRandomCard();
        playerService.addCard(PLAYER_USER, randomCard);
        addCardToPlayer(PLAYER_USER, randomCard);

        if (playerService.isWin(PLAYER_USER)) {
            removeHighlight(PLAYER_USER);

        } else if (playerService.isLose(PLAYER_USER)) {
            removeHighlight(PLAYER_USER);
        } else {
            toggleInteraction(true);
        }
    }

    @FXML
    protected void onStandButtonClick() {
        toggleInteraction(false);

        removeHighlight(PLAYER_USER);

        // Execute dealer logic
        executeBotLogic(PLAYER_DEALER);
    }

    private boolean isUser(int playerId) {
        return playerId == PLAYER_USER;
    }

    private boolean isFirstBot(int playerId) {
        return playerId <= 0;
    }

    private void executeBotLogic(int playerId) {
        VBox playerVbox = (VBox) (vbox.lookup("#box_" + playerId));
        playerVbox.setStyle(StyleConstants.PLAYER_BOX_SELECTED);

        Player player = playerService.getPlayer(playerId);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(3), e -> {
            executeBotHitOrStand(playerId);

            if (player.isReady()) {
                playerVbox.setStyle(StyleConstants.PLAYER_BOX);

                // If last player before user
                if (playerId == PLAYER_COUNT - 3) {
                    toggleInteraction(true);
                }

                if (playerId == PLAYER_DEALER) {
                    List<Player> winnerList = playerService.identifyWinners();

                    if (winnerList.size() == 1) {
                        if (winnerList.get(0).equals(playerService.getPlayer(PLAYER_USER))) {
                            // ToDo: Display win message
                            primaryText.setText("Congratulations");
                            secondaryText.setText("You shamelessly won this round.");
                        } else {
                            // ToDo: Display lose message for user (someone else won)
                            primaryText.setText("Loser :(");
                            secondaryText.setText("You'd better find some place to work. You suck at blackjack.");
                        }
                    } else {
                        if (winnerList.contains(playerService.getPlayer(PLAYER_USER))) {
                            // ToDo:  Display win message
                            primaryText.setText("Congratulations");
                            secondaryText.setText("You're one of the winners :)");
                        } else {
                            // ToDo: Display lose message for user (someone else won)
                            primaryText.setText("Loser :(");
                            secondaryText.setText("You'd better find some place to work. You suck at blackjack.");
                        }
                    }

                    startButton.setDisable(false);
                } else {
                    VBox userVbox = (VBox) (vbox.lookup("#box_" + (playerId + 1)));
                    userVbox.setStyle(StyleConstants.PLAYER_BOX_SELECTED);
                }

                timeline.stop();
            }
        }));


        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void executeBotHitOrStand(int playerId) {
        Player player = playerService.getPlayer(playerId);

        if (playerService.getDeckValue(player.getLocalDeck()) < 17) {
            Card randomCard = cardService.popRandomCard();
            playerService.addCard(playerId, randomCard);

            primaryText.setText(cardService.mapCardToString(randomCard));
            secondaryText.setText("Player " + playerId + " decided to hit and got: ");

            addCardToPlayer(playerId, randomCard);
            player.setReady(false);
        } else {
            if (decisionService.generateRiskDecision(RISK_DECISION).equals(Decision.YES)) {
                Card randomCard = cardService.popRandomCard();
                playerService.addCard(playerId, randomCard);

                primaryText.setText(cardService.mapCardToString(randomCard));
                secondaryText.setText("Player " + playerId + " risked it and got: ");
            } else {
                // ToDo: Check if this works
                primaryText.setText("Stand");
                secondaryText.setText("Player " + playerId + " decides to stand.");
            }
            player.setReady(true);
        }
    }

    private void removeHighlight(int playerId) {
        VBox userVbox = (VBox) (vbox.lookup("#box_" + (playerId)));
        userVbox.setStyle(StyleConstants.PLAYER_BOX);
    }

    private void addCardToPlayer(int playerId, Card card) {
        HBox hbox = (HBox) (vbox.lookup("#cards_" + playerId));
        Label cardLabel = new Label();
        cardLabel.setText(cardService.mapCardToString(card));

        if (card.getCardShape().equals(CardShape.DIAMONDS) || card.getCardShape().equals(CardShape.HEARTS)) {
            cardLabel.setTextFill(Color.color(1, 0, 0));
        } else if (card.getCardShape().equals(CardShape.CLUBS) || card.getCardShape().equals(CardShape.SPADES)) {
            cardLabel.setTextFill(Color.color(0, 0, 0));
        }

        if (((Label) hbox.getChildren().get(0)).getText().equals(MessageConstants.NO_CARDS)) {
            hbox.getChildren().remove(0);
        }

        hbox.getChildren().add(playerService.getPlayer(playerId).getLocalDeck().size() - 1, cardLabel);
    }

    private void toggleInteraction(boolean flag) {
        hitButton.setDisable(!flag);
        standButton.setDisable(!flag);
    }
}