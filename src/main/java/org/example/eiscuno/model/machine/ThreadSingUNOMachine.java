package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.example.eiscuno.controller.Animations;
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
                checkIfPlayerHasOneCard();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Esto realmente se puede borrar, no se porque lo deje aqui xd
     */

    private void checkIfPlayerHasOneCard() {
        if (cardsPlayer.size() == 1 &&
                gameUnoController.unoCheckStarted &&
                !gameUnoController.playerSaidUNO &&
                !alreadyNotified) {

            alreadyNotified = true;

            Platform.runLater(() -> {
                Card penaltyCard = gameUnoController.deck.takeCard(); // ← usar la variable directamente
                gameUnoController.saveGame();
                gameUnoController.humanPlayer.getCardsPlayer().add(penaltyCard);

                Animations.animateCardFromDeck(
                        Card.getBackImage(),
                        gameUnoController.imageViewDeck,             // ← usar directamente
                        gameUnoController.stackPaneCardsPlayer,     // ← usar directamente
                        false,
                        () -> gameUnoController.printCardsHumanPlayer()
                );

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("UNO");
                alert.setHeaderText("¡La máquina te cantó UNO!");
                alert.setContentText("No dijiste UNO a tiempo. Has sido penalizado con una carta.");
                alert.showAndWait();
            });
        }

        if (cardsPlayer.size() != 1) {
            alreadyNotified = false;
        }
    }
}