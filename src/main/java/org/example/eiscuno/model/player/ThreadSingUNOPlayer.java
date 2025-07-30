package org.example.eiscuno.model.player;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.example.eiscuno.controller.Animations;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.exceptions.PenaltyException;
import org.example.eiscuno.model.card.Card;

import java.util.Random;

public class ThreadSingUNOPlayer extends Thread {

    private final MachinePlayer machinePlayer;
    private final Deck deck;
    private final GameUnoController controller;

    public static boolean unoCheckMachineStarted = false;
    public static boolean machineSaidUNO = false;

    public ThreadSingUNOPlayer(GameUnoController controller, MachinePlayer machinePlayer, Deck deck) {
        this.controller = controller;
        this.machinePlayer = machinePlayer;
        this.deck = deck;
    }

    @Override
    public void run() {
        try {
            unoCheckMachineStarted = true;
            machineSaidUNO = false;

            System.out.println("La máquina tiene solo una carta. Esperando si el jugador le canta...");

            int delay = 1000 + new Random().nextInt(2000);
            Thread.sleep(delay);

            if (!machineSaidUNO) {
                machineSaidUNO = true;
                System.out.println("La máquina dijo UNO a tiempo.");
            } else {
                try {
                    throw new PenaltyException("La máquina no dijo UNO a tiempo.", "MACHINE");
                } catch (PenaltyException e) {
                    Platform.runLater(() -> {
                        if (e.getPenalizedEntity().equals("MACHINE")) {
                            machinePlayer.addCard(deck.takeCard());
                            controller.saveGame();

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Penalización a la Máquina");
                            alert.setHeaderText("¡Le cantaste UNO primero a la máquina!");
                            alert.setContentText("La máquina fue penalizada con una carta.");
                            alert.showAndWait();
                        }
                    });
                }
            }

            unoCheckMachineStarted = false;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}