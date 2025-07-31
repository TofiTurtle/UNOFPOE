package org.example.eiscuno.model.table;
import org.example.eiscuno.model.card.Card;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the game table where UNO cards are played and managed.
 * <p>
 * This class handles the cards currently in play and validates card plays
 * according to UNO game rules. It implements {@link Serializable} to support
 * game state persistence.
 * </p>
 */
public class Table implements Serializable {
    private ArrayList<Card> cardsTable;

    /**
     * Constructs a new empty table with no cards.
     */
    public Table() {
        this.cardsTable = new ArrayList<Card>();
    }

    /**
     * Adds a card to the table (play area).
     *
     * @param card the card to be placed on the table
     */
    public void addCardOnTheTable(Card card) {
        this.cardsTable.add(card);
    }

    /**
     * Validates if a card can be played on the current table card according to UNO rules.
     * <p>
     * A play is valid if:
     * <ul>
     *   <li>The card is a wild card (BLACK color)</li>
     *   <li>The table is empty (only at game start)</li>
     *   <li>The card matches the current card's color</li>
     *   <li>The card matches the current card's value</li>
     * </ul>
     * </p>
     *
     * @param card the card being attempted to play
     * @return true if the play is valid, false otherwise
     */
    public boolean isValidPlay(Card card) {
        // Wild cards (BLACK) can always be played
        if (card.getOriginalColor().equals("BLACK")) {
            this.addCardOnTheTable(card);
            return true;
        }

        // Any card can be played if table is empty (game start)
        if (this.getCardsTable().isEmpty()) {
            this.addCardOnTheTable(card);
            return true;
        }

        Card currentCard = getCurrentCardOnTheTable();

        // Color match validation
        if (currentCard.getColor().equals(card.getColor())) {
            this.addCardOnTheTable(card);
            return true;
        }

        // Value match validation
        if (currentCard.getValue().equals(card.getValue())) {
            this.addCardOnTheTable(card);
            return true;
        }

        return false;
    }

    /**
     * Gets the current top card on the table.
     *
     * @return the card currently on top of the table
     * @throws IndexOutOfBoundsException if there are no cards on the table
     */
    public Card getCurrentCardOnTheTable() throws IndexOutOfBoundsException {
        if (cardsTable.isEmpty()) {
            throw new IndexOutOfBoundsException("No hay cartas en la mesa.");
        }
        return this.cardsTable.get(this.cardsTable.size()-1);
    }

    /**
     * Gets all cards currently on the table.
     *
     * @return an ArrayList containing all cards on the table
     */
    public ArrayList<Card> getCardsTable() {
        return cardsTable;
    }
}