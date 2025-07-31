package org.example.eiscuno.model.player;

import javafx.application.Platform;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.exceptions.PenaltyException;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;

import java.util.List;
import java.util.Random;

/**
 * Thread that handles the UNO calling mechanism for the machine player.
 * <p>
 * This thread simulates the machine player's reaction time when calling UNO
 * and applies penalties if the machine fails to call UNO when required.
 * </p>
 */
public class ThreadSingUNOPlayer extends Thread {

    private final GameUnoController controller;
    private final Player machinePlayer;
    private final List<Card> machineCards;
    private final Deck deck;

    /**
     * Constructs a new ThreadSingUNOPlayer with the specified game components.
     *
     * @param controller the main game controller
     * @param machinePlayer the machine player instance
     * @param machineCards the list of cards held by the machine player
     * @param deck the game deck
     */
    public ThreadSingUNOPlayer(GameUnoController controller, Player machinePlayer,
                               List<Card> machineCards, Deck deck) {
        this.controller = controller;
        this.machinePlayer = machinePlayer;
        this.machineCards = machineCards;
        this.deck = deck;
    }

    /**
     * Main execution method for the thread.
     * <p>
     * Simulates the machine player's reaction time and checks if UNO was called properly,
     * applying penalties if necessary.
     * </p>
     */
    @Override
    public void run() {
        try {
            // Simulate machine reaction time (1-3 seconds)
            int delay = 1000 + new Random().nextInt(2000);
            Thread.sleep(delay);

            // Check if machine hasn't called UNO and wasn't challenged by human player
            if (!controller.isMachineSaidUNO()) {
                Platform.runLater(() -> {
                    try {
                        controller.setMachineSaidUNO(true);
                        System.out.println("La máquina dijo UNO a tiempo.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                // Penalize machine for not calling UNO in time
                Platform.runLater(() -> {
                    try {
                        throw new PenaltyException("La máquina no dijo UNO a tiempo.", "MACHINE");
                    } catch (PenaltyException e) {
                        if (e.getPenalizedEntity().equals("MACHINE")) {
                            machinePlayer.addCard(controller.getDeck().takeCard());
                            controller.saveGame();
                            controller.showPenaltyAlert("Máquina", "¡Le cantaste UNO primero a la máquina!");
                        }
                    }
                });
            }

            // Reset flags after processing
            controller.setUnoCheckMachineStarted(false);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}