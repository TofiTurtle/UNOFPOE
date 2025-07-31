package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.example.eiscuno.controller.Animations;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

/**
 * Thread that handles the machine player's UNO calling mechanism.
 * This thread randomly checks if the human player has only one card left
 * and didn't call UNO, applying a penalty card if needed.
 */
public class ThreadSingUNOMachine implements Runnable {
    private ArrayList<Card> cardsPlayer;
    private GameUnoController gameUnoController; // Reference to the main game controller
    private boolean alreadyNotified = false; // Flag to prevent multiple notifications

    /**
     * Constructs a new ThreadSingUNOMachine with the specified player cards and game controller.
     *
     * @param cardsPlayer the list of cards held by the human player
     * @param gameUnoController the main game controller instance
     */
    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer, GameUnoController gameUnoController) {
        this.cardsPlayer = cardsPlayer;
        this.gameUnoController = gameUnoController;
    }

    /**
     * Main execution method for the thread.
     * Periodically checks if the human player should be penalized for not calling UNO.
     */
    @Override
    public void run() {
        while (true) {
            try {
                // Random delay between checks (0-5 seconds)
                Thread.sleep((long) (Math.random() * 5000));
                checkIfPlayerHasOneCard();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the human player has only one card left and didn't call UNO,
     * applying a penalty card if conditions are met.
     */
    private void checkIfPlayerHasOneCard() {
        if (cardsPlayer.size() == 1 &&
                gameUnoController.unoCheckStarted &&
                !gameUnoController.playerSaidUNO &&
                !alreadyNotified) {

            alreadyNotified = true;

            Platform.runLater(() -> {
                // Apply penalty card to human player
                Card penaltyCard = gameUnoController.deck.takeCard();
                gameUnoController.saveGame();
                gameUnoController.humanPlayer.getCardsPlayer().add(penaltyCard);

                // Animate the penalty card being added to player's hand
                Animations.animateCardFromDeck(
                        Card.getBackImage(),
                        gameUnoController.imageViewDeck,
                        gameUnoController.stackPaneCardsPlayer,
                        false,
                        () -> gameUnoController.printCardsHumanPlayer()
                );

                gameUnoController.showGameAlert("!LA MAQUINA TE HA CANTADO UNO");
            });
        }

        // Reset notification flag if player no longer has one card
        if (cardsPlayer.size() != 1) {
            alreadyNotified = false;
        }
    }
}