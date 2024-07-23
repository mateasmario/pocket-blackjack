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
        scrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> vbox.setPrefWidth(newValue.getWidth()));

        toggleInteraction(false);

        for (int i = 0; i < PLAYER_COUNT; i++) {
            VBox playerBox = new VBox();

            HBox playerTitleHbox = new HBox();
            Label playerTitleLabel = new Label();
            playerTitleLabel.setStyle("-fx-font-weight: bold;");

            String playerName = switch (i) {
                case PLAYER_COUNT - 2 -> "You";
                case PLAYER_COUNT - 1 -> "Dealer";
                default -> "Player " + i;
            };
            playerTitleLabel.setText(playerName);
            playerTitleHbox.getChildren().add(playerTitleLabel);

            HBox playerCardsHbox = new HBox();
            playerCardsHbox.setId("cards_" + i);
            playerCardsHbox.setSpacing(10);

            Label label = new Label();
            label.setText(MessageConstants.NO_CARDS);
            playerCardsHbox.getChildren().add(0, label);

            playerBox.getChildren().add(0, playerTitleHbox);
            playerBox.getChildren().add(1, playerCardsHbox);
            playerBox.setId("box_" + i);

            playerBox.setStyle(StyleConstants.PLAYER_BOX);

            vbox.getChildren().add(playerBox);
        }
    }

    @FXML
    protected void onStartButtonClick() {
        if (playerService.getPlayerList() != null) {
            for(int i = 0; i<PLAYER_COUNT; i++) {
                vbox.getChildren().remove(0);
            }

            initialize();
        }

        startButton.setDisable(true);
        toggleInteraction(false);

        cardService.initializeGlobalDeck();
        playerService.initializePlayerList(PLAYER_COUNT);

        // Bot logic
        final int[] playerId = {0};
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            if (playerId[0] >= PLAYER_COUNT - 2) {
                // Player logic
                timeline.stop();
            } else {
                if (playerId[0] > 0) {
                    Player player = playerService.getPlayer(0);
                    if (player.isReady()) {
                        executeBotLogic(playerId[0]);
                        playerId[0]++;
                    } else {
                        // Player not yet ready (for debugging)
                    }
                } else {
                    executeBotLogic(playerId[0]);
                    playerId[0]++;
                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    @FXML
    protected void onHitButtonClick() {
        toggleInteraction(false);

        Card randomCard = cardService.popRandomCard();
        playerService.addCard(PLAYER_COUNT - 2, randomCard);
        addCardToPlayer(PLAYER_COUNT - 2, randomCard);

        if (playerService.isWin(PLAYER_COUNT - 2)) {
            // Player won. Show win screen
            VBox userVbox = (VBox) (vbox.lookup("#box_" + (PLAYER_COUNT - 2)));
            userVbox.setStyle(StyleConstants.PLAYER_BOX);
        } else if (playerService.isLose(PLAYER_COUNT - 2)) {
            // Player lost. Show lose screen
            startButton.setDisable(false);
            VBox userVbox = (VBox) (vbox.lookup("#box_" + (PLAYER_COUNT - 2)));
            userVbox.setStyle(StyleConstants.PLAYER_BOX);
        } else {
            toggleInteraction(true);
        }
    }

    @FXML
    protected void onStandButtonClick() {
        toggleInteraction(false);

        VBox playerVbox = (VBox) (vbox.lookup("#box_" + (PLAYER_COUNT - 2)));
        playerVbox.setStyle(StyleConstants.PLAYER_BOX);

        // Execute dealer logic
        executeBotLogic(PLAYER_COUNT - 1);
    }

    private void executeBotLogic(int playerId) {
        VBox playerVbox = (VBox) (vbox.lookup("#box_" + playerId));
        playerVbox.setStyle(StyleConstants.PLAYER_BOX_SELECTED);

        Player player = playerService.getPlayer(playerId);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(3), e -> {
            if (playerService.getDeckValue(playerService.getPlayer(playerId).getLocalDeck()) < 17) {
                Card randomCard = cardService.popRandomCard();
                playerService.addCard(playerId, randomCard);

                addCardToPlayer(playerId, randomCard);
                player.setReady(false);
            } else {
                if (decisionService.generateRiskDecision(RISK_DECISION).equals(Decision.YES)) {
                    Card randomCard = cardService.popRandomCard();
                    playerService.addCard(playerId, randomCard);
                } else {
                    // Show a message with the bot standing
                }
                player.setReady(true);
            }

            if (player.isReady()) {
                playerVbox.setStyle(StyleConstants.PLAYER_BOX);

                // If last player before user
                if (playerId == PLAYER_COUNT - 3) {
                    toggleInteraction(true);
                }

                // If dealer
                if (playerId == PLAYER_COUNT - 1) {
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

                    startButton.setDisable(false);
                }
                else {
                    VBox userVbox = (VBox) (vbox.lookup("#box_" + (playerId + 1)));
                    userVbox.setStyle(StyleConstants.PLAYER_BOX_SELECTED);
                }

                timeline.stop();
            }
        }));


        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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