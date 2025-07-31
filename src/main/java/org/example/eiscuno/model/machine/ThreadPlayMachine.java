package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.controller.Animations;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javafx.scene.layout.StackPane;

/**
 * Thread that handles the machine player's turn in the UNO game.
 * This thread manages the machine's card plays, special card handling,
 * and turn transitions between the machine and human player.
 */
public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private Deck deck;
    private volatile boolean hasPlayerPlayed;
    private GameUnoController gameUnoController;
    private Map<Card, ImageView> machineCardViews;
    private Pane stackPaneCardsMachine;

    /**
     * Constructs a new ThreadPlayMachine with the specified game components.
     *
     * @param table the game table where cards are played
     * @param machinePlayer the machine player instance
     * @param tableImageView the ImageView representing the table
     * @param deck the game deck
     * @param gameUnoController the main game controller
     * @param machineCardViews mapping of cards to their ImageViews for the machine player
     * @param stackPaneCardsMachine the pane containing the machine's cards
     */
    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, Deck deck,
                             GameUnoController gameUnoController, Map<Card, ImageView> machineCardViews,
                             Pane stackPaneCardsMachine) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
        this.deck = deck;
        this.gameUnoController = gameUnoController;
        this.machineCardViews = machineCardViews;
        this.stackPaneCardsMachine = stackPaneCardsMachine;
    }

    /**
     * Main execution method for the thread.
     * Handles the machine player's turn logic including card plays,
     * special card handling, and turn transitions.
     */
    @Override
    public void run() {
        while (gameUnoController.gameUno.isGameOver() == 0) {
            if (hasPlayerPlayed) {
                // Disable human player UI during machine's turn
                Platform.runLater(() -> gameUnoController.stackPaneCardsPlayer.setDisable(true));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Card cardPlayed = putCardOnTheTable();

                Platform.runLater(() -> gameUnoController.printCardsMachinePlayer());

                if (cardPlayed == null) {
                    handleTakeCard();
                    Platform.runLater(() -> {
                        gameUnoController.getLabelAlertMachine().setText("La maquina arrastró una carta");
                    });
                    hasPlayerPlayed = false;
                }
                else if (cardPlayed.isSpecial()) {
                    // Wait for special card handling to complete in FX thread
                    CountDownLatch latch = new CountDownLatch(1);
                    Platform.runLater(() -> {
                        gameUnoController.handleSpecialCard(cardPlayed, gameUnoController.getHumanPlayer());
                        latch.countDown();
                        deck.PushToAuxDeck(cardPlayed);
                        gameUnoController.saveGame(); // Save game state
                    });
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                else {
                    // Normal card - pass turn to human player
                    gameUnoController.imageViewDeck.setOpacity(1);
                    gameUnoController.buttonDeck.setDisable(false);
                    hasPlayerPlayed = false;
                    deck.PushToAuxDeck(cardPlayed);
                    gameUnoController.saveGame(); // Save game state
                }

                if (gameUnoController.gameUno.isGameOver() == 1 || gameUnoController.gameUno.isGameOver() == 2) {
                    gameUnoController.stackPaneCardsPlayer.setDisable(true);
                    gameUnoController.deactivateEmptyDeck();
                    if (gameUnoController.gameUno.isGameOver() == 1) {
                        gameUnoController.showGameAlert("*-*-*- GANO LA MAQUINA... *-*-*-");
                    } else {
                        gameUnoController.showGameAlert("*-*-*- GANO EL JUGADOR, FELICIDADES! *-*-*-");
                    }
                }

                // Re-enable human player UI
                Platform.runLater(() -> gameUnoController.stackPaneCardsPlayer.setDisable(false));
            }
        }
    }

    /**
     * Attempts to play a valid card from the machine's hand to the table.
     *
     * @return the card that was played, or null if no valid card was found
     */
    private Card putCardOnTheTable() {
        // Create a copy of the machine's current deck for iteration
        ArrayList<Card> machineDeck = new ArrayList<>(machinePlayer.getCardsPlayer());

        // Debug output of machine's deck before playing
        System.out.println("----------------------------------------------\n" +
                "       Mazo Maquina Antes de Lanzar: ");
        for (int i = 0; i < machineDeck.size(); i++) {
            System.out.print(machineDeck.get(i).getColor() + ": " + machineDeck.get(i).getValue() + "\n");
        }
        System.out.println("----------------------------------------------\n");

        int index = (int) (Math.random() * machineDeck.size());
        Card selectedCard;

        // Iterate through machine's deck to find a playable card
        for (int i = 0; i < machineDeck.size(); i++) {
            selectedCard = machineDeck.get(i);
            if (table.isValidPlay(selectedCard)) {
                Card finalSelectedCard = selectedCard;
                final ImageView finalTableImage = tableImageView;

                // Remove valid card from machine's hand and set it on the table
                machinePlayer.getCardsPlayer().remove(selectedCard);
                tableImageView.setImage(selectedCard.getImage());

                Platform.runLater(() -> {
                    ImageView iv = machineCardViews.get(finalSelectedCard);

                    if (iv != null) {
                        // Ensure card is in Pane before animating
                        if (!stackPaneCardsMachine.getChildren().contains(iv)) {
                            stackPaneCardsMachine.getChildren().add(iv);
                        }

                        Animations.playCardFromMachine(finalSelectedCard, iv, finalTableImage, () -> {
                            Platform.runLater(() -> stackPaneCardsMachine.getChildren().remove(iv));
                        });
                    } else {
                        System.out.println("⚠ No se encontró la carta en machineCardViews");
                    }
                });

                // Debug output of machine's deck after playing
                System.out.println("----------------------------------------------\n" +
                        "       Mazo Maquina DESPUES de Lanzar: ");
                for (int j = 0; j < machinePlayer.getCardsPlayer().size(); j++) {
                    System.out.print(machinePlayer.getCardsPlayer().get(j).getColor() + ": " +
                            machinePlayer.getCardsPlayer().get(j).getValue() + "\n");
                }
                System.out.println("----------------------------------------------\n");

                // Update UI with play information
                Platform.runLater(() -> {
                    gameUnoController.getLabelAlertMachine().setText("La maquina jugó una carta");
                });

                return selectedCard;
            }
        }

        // Return null if no valid card was found
        return null;
    }

    /**
     * Handles the machine drawing a card from the deck when it cannot play any card.
     * Includes animation and updates the game state accordingly.
     */
    private void handleTakeCard() {
        if (deck.isEmpty()) {
            System.out.println("El mazo esta vacio, no se puede arrastrar");
            gameUnoController.deactivateEmptyDeck();
        } else {
            Platform.runLater(() -> {
                // Animation with face-down card for the machine
                Image cardBackImage = new Image("/org/example/eiscuno/cards-uno/card_uno.png");
                Animations.animateCardFromDeck(
                        cardBackImage,
                        gameUnoController.imageViewDeck,
                        gameUnoController.stackPaneCardsMachine,
                        true, // is machine
                        () -> {
                            // Add drawn card to machine's hand
                            machinePlayer.addCard(deck.takeCard());
                            gameUnoController.saveGame(); // Save game state

                            gameUnoController.printCardsMachinePlayer();

                            gameUnoController.imageViewDeck.setOpacity(1);
                            gameUnoController.buttonDeck.setDisable(false);

                            setHasPlayerPlayed(false);
                        }
                );
            });
        }
    }

    /**
     * Returns whether the player has played their turn.
     *
     * @return true if the player has played, false otherwise
     */
    public boolean getHasPlayerPlay() {
        return this.hasPlayerPlayed;
    }

    /**
     * Sets whether the player has played their turn.
     *
     * @param hasPlayerPlayed true if the player has played, false otherwise
     */
    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}