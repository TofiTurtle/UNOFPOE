package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private Deck deck;
    private volatile boolean hasPlayerPlayed;
    private GameUnoController gameUnoController;

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, Deck deck, GameUnoController gameUnoController) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
        this.deck = deck;
        this.gameUnoController = gameUnoController;
    }

    public void run() {
        while (true) {
            if (hasPlayerPlayed) {
                // desactivar UI del jugador
                Platform.runLater(() -> gameUnoController.gridPaneCardsPlayer.setDisable(true));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Card cardPlayed = putCardOnTheTable();

                Platform.runLater(() -> gameUnoController.printCardsMachinePlayer());

                if (cardPlayed == null) {
                    handleTakeCard();
                }
                else if (cardPlayed.isSpecial()) {
                    // esperamos a que handleSpecialCard termine de ejecutarse en el FX‐thread
                    CountDownLatch latch = new CountDownLatch(1);
                    Platform.runLater(() -> {
                        gameUnoController.handleSpecialCard(cardPlayed, gameUnoController.getHumanPlayer());
                        latch.countDown();
                        //La maquina aqui PUSO una carta ESPECIAL, como PUSO-> la guardamos en el auxiliar
                        deck.PushToAuxDeck(cardPlayed);
                        System.out.println("*/*/*/*/*/*/*/*/CANTIDAD DE CARTAS EN EL MAZO AUXILIAR: "+ deck.getAuxDeckSize());
                    });
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                else {
                    // carta normal → pasa el turno a humano
                    gameUnoController.buttonDeck.setDisable(false);
                    hasPlayerPlayed = false;
                    //Aqui la maquina tiro una carta NORMAL, como PUSO-> Guardamos en auxiliar
                    deck.PushToAuxDeck(cardPlayed);
                    System.out.println("*/*/*/*/*/*/*/*/CANTIDAD DE CARTAS EN EL MAZO AUXILIAR: "+ deck.getAuxDeckSize());
                }

                // reactivar UI del jugador
                Platform.runLater(() -> gameUnoController.gridPaneCardsPlayer.setDisable(false));
            }
        }
    }

    // este metodo devuelve la carta que se jugo o null en el caso de que no tuviera carta valida para jugar
    private Card putCardOnTheTable(){
        
        //se crea una copia de el mazo actual de la maquina para iterar sobre esta
        ArrayList<Card> machineDeck = new ArrayList<>(machinePlayer.getCardsPlayer());
        //Para verificar
        System.out.println("----------------------------------------------\n" +
                           "       Mazo Maquina Antes de Lanzar: ");
        for(int i = 0; i < machineDeck.size(); i++) {
            System.out.print( machineDeck.get(i).getColor() + ": " + machineDeck.get(i).getValue() + "\n");
        }
        System.out.println("----------------------------------------------\n");

        int index = (int) (Math.random() * machineDeck.size());
        Card selectedCard;


        //iteramos sobre el mazo de la maquina uno por uno comprobando  que se pueda lanzar una carta
        for(int i = 0; i < machineDeck.size(); i++) {
            selectedCard = machineDeck.get(i);
            if(table.isValidPlay(selectedCard)) {
                //si la carta fue valida entonces la borro de el mazo original de la maquina y la seteo en la mesa
                machinePlayer.getCardsPlayer().remove(selectedCard);
                tableImageView.setImage(selectedCard.getImage());


                System.out.println("----------------------------------------------\n" +
                        "       Mazo Maquina DESPUES de Lanzar: ");
                for(int j = 0; j < machinePlayer.getCardsPlayer().size(); j++) {
                    System.out.print( machinePlayer.getCardsPlayer().get(j).getColor() + ": " + machinePlayer.getCardsPlayer().get(j).getValue() + "\n");
                }
                System.out.println("----------------------------------------------\n");

                //retorno la carta jugada
                return selectedCard;
            }
        }

        // Si en el ciclo anterior no se logro tirar ninguna carta, entonces devuelve null
        return null;
    }

    /*
    Metodo que maneja el que la maquina tome una carta y despues ceda el turno al jugador
     */
    private void handleTakeCard() {
        if(deck.isEmpty()) {
            System.out.println("El mazo esta vacio, no se puede arrastrar");
            gameUnoController.deactivateEmptyDeck();
        }
        else {
            machinePlayer.addCard(deck.takeCard());
            //activar el boton para que el jugador pueda arrastrar
            gameUnoController.buttonDeck.setDisable(false);
            setHasPlayerPlayed(false);
        }
    }
    public boolean getHasPlayerPlay() {
        return this.hasPlayerPlayed;
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}