package org.example.eiscuno.model.saveGame;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the complete state of an UNO game that can be saved and restored.
 * <p>
 * This class implements {@link Serializable} to allow game state persistence.
 * It contains all necessary components to restore a game to its exact state.
 * </p>
 */
public class GameState implements Serializable {
    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;

    /**
     * Constructs a new GameState with the specified game components.
     *
     * @param humanPlayer the human player's current state
     * @param machinePlayer the machine player's current state
     * @param deck the current state of the game deck
     * @param table the current state of the game table
     */
    public GameState(Player humanPlayer, Player machinePlayer, Deck deck, Table table) {
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.deck = deck;
        this.table = table;
    }

    /**
     * Gets the human player's game state.
     *
     * @return the human player instance with current cards and state
     */
    public Player getHumanPlayer() {
        return humanPlayer;
    }

    /**
     * Gets the machine player's game state.
     *
     * @return the machine player instance with current cards and state
     */
    public Player getMachinePlayer() {
        return machinePlayer;
    }

    /**
     * Gets the current deck state.
     *
     * @return the deck instance with current cards
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Gets the current table state.
     *
     * @return the table instance with current played cards
     */
    public Table getTable() {
        return table;
    }
}