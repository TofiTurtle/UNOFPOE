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

                // ahora obtenemos la carta que se jugo
                Card cardPlayed = putCardOnTheTable();

                if(cardPlayed.isWild()) {
                    String wildEffect = gameUnoController.handleWildCard(cardPlayed,gameUnoController.getHumanPlayer());
                    if(!(wildEffect.equals("SKIP") || wildEffect.equals("WILD") || wildEffect.equals("RESERVE"))) {
                        hasPlayerPlayed = false;
                    }
                }
                else {
                    hasPlayerPlayed = false;
                }


                //aqui volvemos a habilitar el mazo de el jugador
                Platform.runLater(() -> {
                    gameUnoController.gridPaneCardsPlayer.setDisable(false);
                });
            }
        }
    }

    // este metodo ahora retorna la carta que se jugo, para despues hacer comprobaciones de si es comodin o que
    private Card putCardOnTheTable(){
        /*
        La idea seria coger en una estructura de datos y poner las cartas actuales de la maquina, entonces en el
        do while que escoja aleatoriamente y si no funciona la quite de esta estructura de datos, mas no de su mazo
        y entonces si este mazo se queda vacio pues ahi si empezar a coger de la baraja de a una carta y ir probando hasta
        que una funcione
         */

        //Aqui se crea una copia de las cartas en la baraja actual
        ArrayList<Card> temporaryDeck = new ArrayList<>(machinePlayer.getCardsPlayer());
        //Para verificar
        for(int i = 0; i < temporaryDeck.size(); i++) {
            System.out.print( temporaryDeck.get(i).getColor() + " : " + temporaryDeck.get(i).getValue() + "  ,,,, ");
        }

        int index = (int) (Math.random() * temporaryDeck.size());
        Card selectedCard;

        do {
            /*
            En este punto la maquina hace random para coger aleatoriamente sus cartas hasta que sea valido ponerlas
            Aqui hay un problema y esque si no tiene ninguna valida esto es infinito, entonces para esto hay que usar la baraja, pero despues
            de probar con todas sus cartas
             */

            /*
            Si el temporarydeck esta vacio es porque ya usamos todas las cartas y ninguna funciono
             */
            if (temporaryDeck.isEmpty()) {
                selectedCard = deck.takeCard();
                machinePlayer.addCard(selectedCard);
            } else {
                index = (int) (Math.random() * temporaryDeck.size());
                selectedCard = temporaryDeck.get(index);
                temporaryDeck.remove(index);
            }

            System.out.println("lo intenta");
        } while(!table.isValidPlay(selectedCard));

        // Verificar que si se esten borrando correctamente, esto se puede borrar despues
        for(int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
            System.out.print( machinePlayer.getCard(i).getColor() + " : " + machinePlayer.getCard(i).getValue() + "  ,,,, ");
        }
        System.out.println();

        // esto no, esta es la logica de borrar
        machinePlayer.getCardsPlayer().remove(selectedCard);
        tableImageView.setImage(selectedCard.getImage());

        // esto tambien se puede borrar, solo es para verficar
        for(int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
            System.out.print( machinePlayer.getCard(i).getColor() + " : " + machinePlayer.getCard(i).getValue() + "  ,,,, ");
        }

        return selectedCard;
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}