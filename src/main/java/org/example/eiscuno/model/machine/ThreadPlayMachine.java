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

                putCardOnTheTable();
                hasPlayerPlayed = false;

                //aqui volvemos a habilitar el mazo de el jugador
                Platform.runLater(() -> {
                    gameUnoController.gridPaneCardsPlayer.setDisable(false);
                });
            }
        }
    }

    private void putCardOnTheTable(){
        /*
        La idea seria coger en una estructura de datos y poner las cartas actuales de la maquina, entonces en el
        do while que escoja aleatoriamente y si no funciona la quite de esta estructura de datos, mas no de su mazo
        y entonces si este mazo se queda vacio pues ahi si empezar a coger de la baraja de a una carta y ir probando hasta
        que una funcione
         */

        //Aqui se crea una copia de las cartas en la baraja actual
        ArrayList<Card> temporaryDeck = new ArrayList<>(machinePlayer.getCardsPlayer());
        //Para verificar
        System.out.println("----------------------------------------------\n" +
                           "       Mazo Maquina Antes de Lanzar: ");
        for(int i = 0; i < temporaryDeck.size(); i++) {
            System.out.print( temporaryDeck.get(i).getColor() + ": " + temporaryDeck.get(i).getValue() + "\n");
        }
        System.out.println("----------------------------------------------\n");

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

        machinePlayer.getCardsPlayer().remove(selectedCard);
        tableImageView.setImage(selectedCard.getImage());

        //Logica Para cartas EAT**
        if (selectedCard.getValue().equals("TWO_WILD")) {
            gameUnoController.gameUno.eatCard(gameUnoController.getHumanPlayer(), 2);
        }

        // Verificar que si se esten borrando correctamente
        System.out.println("\n----------------------------------------------\n" +
                           "       Mazo Maquina DESPUES de Lanzar: ");
        for(int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
            System.out.print( machinePlayer.getCard(i).getColor() + ": " + machinePlayer.getCard(i).getValue() + "\n");
        }
        System.out.println("-------------------------------------------------------\n\n");

    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}