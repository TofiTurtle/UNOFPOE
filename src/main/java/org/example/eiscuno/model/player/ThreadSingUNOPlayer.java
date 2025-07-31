package org.example.eiscuno.model.player;

import javafx.application.Platform;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.exceptions.PenaltyException;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;

import java.util.List;
import java.util.Random;

public class ThreadSingUNOPlayer extends Thread {

    private final GameUnoController controller;
    private final Player machinePlayer;
    private final List<Card> machineCards;
    private final Deck deck;

    public ThreadSingUNOPlayer(GameUnoController controller, Player machinePlayer, List<Card> machineCards, Deck deck) {
        this.controller = controller;
        this.machinePlayer = machinePlayer;
        this.machineCards = machineCards;
        this.deck = deck;
    }

    @Override
    public void run() {
        try {
            // Simular tiempo de reacción de la máquina
            int delay = 1000 + new Random().nextInt(2000);
            Thread.sleep(delay);

            // Si la máquina todavía no dijo UNO y el jugador no la acusó
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
                // Penalizar a la máquina si no dijo UNO
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

            // Al terminar, se resetean las banderas
            controller.setUnoCheckMachineStarted(false);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
