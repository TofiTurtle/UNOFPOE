package org.example.eiscuno.model.saveGame;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;

    public GameState(Player humanPlayer, Player machinePlayer, Deck deck, Table table) {
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.deck = deck;
        this.table = table;
    }
    public Player getHumanPlayer() {
        return humanPlayer;
    }
    public Player getMachinePlayer() {
        return machinePlayer;
    }
    public Deck getDeck() {
        return deck;
    }
    public Table getTable() {
        return table;
    }



}
