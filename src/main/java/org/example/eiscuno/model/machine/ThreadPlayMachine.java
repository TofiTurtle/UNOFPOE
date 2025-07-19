package org.example.eiscuno.model.machine;

import javafx.scene.image.ImageView;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private volatile boolean hasPlayerPlayed;

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
    }

    public void run() {
        while (true){
            if(hasPlayerPlayed){
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Aqui iria la logica de colocar la carta
                putCardOnTheTable();
                hasPlayerPlayed = false;
            }
        }
    }

    private void putCardOnTheTable(){
        int index;
        Card card;
        do {
            /*
            En este punto la maquina hace random para coger aleatoriamente sus cartas hasta que sea valido ponerlas
            Aqui hay un problema y esque si no tiene ninguna valida esto es infinito, entonces para esto hay que usar la baraja, pero despues
            de probar con todas sus cartas, metodo proximo
             */
            index = (int) (Math.random() * machinePlayer.getCardsPlayer().size());
            card = machinePlayer.getCard(index);
            System.out.println("lo intenta");
        } while(!table.isValidPlay(card));
        tableImageView.setImage(card.getImage());
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}