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
        while (true){
            if(hasPlayerPlayed){
                // esto lo que hace es desactivar las cartas del jugador para que no pueda seguir poniendo cartas
                Platform.runLater(() -> {
                    gameUnoController.gridPaneCardsPlayer.setDisable(true);
                });
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // ahora obtenemos la carta que se jugo, esto tambien peude ser null
                Card cardPlayed = putCardOnTheTable();

                //si es null entonces arrastramos
                if (cardPlayed == null) {
                    handleTakeCard();
                }
                else {
                    //hacemos las comprobaciones de si es una carta comodin
                    if(cardPlayed.isWild()) {
                        String wildEffect = gameUnoController.handleWildCard(cardPlayed,gameUnoController.getHumanPlayer());
                        if(!(wildEffect.equals("SKIP") || wildEffect.equals("WILD") || wildEffect.equals("RESERVE"))) {
                            gameUnoController.buttonDeck.setDisable(false);
                            hasPlayerPlayed = false;
                        }
                    }
                    else {
                        gameUnoController.buttonDeck.setDisable(false);
                        hasPlayerPlayed = false;
                    }
                }

                //aqui volvemos a habilitar el mazo de el jugador
                Platform.runLater(() -> {
                    gameUnoController.gridPaneCardsPlayer.setDisable(false);
                });
            }
        }
    }

    // este metodo devuelve la carta que se jugo o null en el caso de que no tuviera carta valida para jugar
    private Card putCardOnTheTable(){
        Card selectedCard;

        //se crea una copia de el mazo actual de la maquina para iterar sobre esta
        ArrayList<Card> machineDeck = new ArrayList<>(machinePlayer.getCardsPlayer());

        //Para verificar comportamiento
        for(int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
            System.out.print( machinePlayer.getCardsPlayer().get(i).getColor() + " : " + machinePlayer.getCardsPlayer().get(i).getValue() + "  ,,,, ");
        }
        System.out.println();

        //iteramos sobre el mazo de la maquina uno por uno comprobando  que se pueda lanzar una carta
        for(int i = 0; i < machineDeck.size(); i++) {
            selectedCard = machineDeck.get(i);
            if(table.isValidPlay(selectedCard)) {
                //si la carta fue valida entonces la borro de el mazo original de la maquina y la seteo en la mesa
                machinePlayer.getCardsPlayer().remove(selectedCard);
                tableImageView.setImage(selectedCard.getImage());
                for(int j = 0; j < machinePlayer.getCardsPlayer().size(); j++) {
                    System.out.print( machinePlayer.getCardsPlayer().get(j).getColor() + " : " + machinePlayer.getCardsPlayer().get(j).getValue() + "  ,,,, ");
                }
                System.out.println();
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
        machinePlayer.addCard(deck.takeCard());
        //activar el boton para que el jugador pueda arrastrar
        gameUnoController.buttonDeck.setDisable(false);
        setHasPlayerPlayed(false);
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}