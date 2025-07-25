package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

public class ThreadSingUNOMachine implements Runnable{
    private ArrayList<Card> cardsPlayer;
    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer, GameUnoController gameUnoController){
        this.cardsPlayer = cardsPlayer;
        this.gameUnoController = gameUnoController;
    }
    private GameUnoController gameUnoController; // Referencia al controlador principal
    private boolean alreadyNotified = false; // Para evitar múltiples notificaciones


    @Override
    public void run(){
        while (true){
            try {
                Thread.sleep((long) (Math.random() * 5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hasOneCardTheHumanPlayer();
        }
    }

    /**
     * Este metodo lo llama un hilo en segundo plano para verificar si el jugador tiene
     * exactamente una carta y no ha dicho UNO, y en ese caso penalizarlo si no fue más rápido que la máquina.
     */
    private void hasOneCardTheHumanPlayer() {
        // Verifica si el jugador tiene exactamente una carta,
        // no ha dicho UNO, y ya está activo el chequeo
        if (cardsPlayer.size() == 1 && gameUnoController.unoCheckStarted &&
                !gameUnoController.playerSaidUNO && !alreadyNotified) {

            alreadyNotified = true;

            Platform.runLater(() -> {
                System.out.println("La máquina acusó al jugador por no decir UNO.");

                // Penaliza al jugador
                gameUnoController.humanPlayer.addCard(gameUnoController.deck.takeCard());
                gameUnoController.printCardsHumanPlayer();

                // Muestra alerta
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("¡No dijiste UNO!");
                alert.setHeaderText(null);
                alert.setContentText("La máquina te acusó por no decir UNO. Se te agregó una carta.");
                alert.showAndWait();

                // Reinicia banderas
                gameUnoController.playerSaidUNO = false;
                gameUnoController.unoCheckStarted = false;
                alreadyNotified = false;
            });
        }

        // Si vuelve a tener más de una carta, reinicia la posibilidad de acusar
        if (cardsPlayer.size() != 1) {
            alreadyNotified = false;
        }
    }
}