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
     * Esto realmente se puede borrar, no se porque lo deje aqui xd
     */
    private void hasOneCardTheHumanPlayer() {
        if (cardsPlayer.size() == 1 &&
                gameUnoController.unoCheckStarted &&
                !gameUnoController.playerSaidUNO &&
                !alreadyNotified) {

            alreadyNotified = true;
        }

        // Reinicia cuando ya no hay solo una carta
        if (cardsPlayer.size() != 1) {
            alreadyNotified = false;
        }
    }
}
