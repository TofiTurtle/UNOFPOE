package org.example.eiscuno.model.table;

import org.example.eiscuno.model.card.Card;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the table in the Uno game where cards are played.
 */
public class Table implements Serializable {
    private ArrayList<Card> cardsTable;

    /**
     * Constructs a new Table object with no cards on it.
     */
    public Table(){
        this.cardsTable = new ArrayList<Card>();
    }

    /**
     * Adds a card to the table.
     *
     * @param card The card to be added to the table.
     */
    public void addCardOnTheTable(Card card){
        this.cardsTable.add(card);
    }


    /**
     * Verifica si una carta puede ser jugada sobre la carta actual en la mesa.
     * @param card La carta que el jugador intenta jugar
     * @return true si la jugada es válida, false si no lo es
     */
    public boolean isValidPlay(Card card) {
        // Si la carta jugada originalmente es negra (comodín o +4), se puede jugar siempre
        if (card.getOriginalColor().equals("BLACK")) {
            this.addCardOnTheTable(card); //Se coloca la carta sobre la mesa
            return true;
        }

        //Si la mesa está vacía, se permite colocar cualquier carta (normalmente solo al inicio del juego)
        if (this.getCardsTable().isEmpty()) {
            this.addCardOnTheTable(card);
            return true;
        }

        //Se obtiene la carta actualmente en la parte superior de la pila de la mesa
        Card currentCard = getCurrentCardOnTheTable();

        //Si la carta jugada coincide en color con la carta actual en la mesa
        if (currentCard.getColor().equals(card.getColor())) {
            this.addCardOnTheTable(card); // Se coloca la carta sobre la mesa
            return true;
        }

        //Si la carta jugada coincide en valor con la carta actual en la mesa (por ejemplo, dos "3")
        if (currentCard.getValue().equals(card.getValue())) {
            this.addCardOnTheTable(card); // Se coloca la carta sobre la mesa
            return true;
        }

        //Si no cumple ninguna de las condiciones anteriores, la jugada no es válida
        return false;
    }

    /**
     * Retrieves the current card on the table.
     *
     * @return The card currently on the table.
     * @throws IndexOutOfBoundsException if there are no cards on the table.
     */
    public Card getCurrentCardOnTheTable() throws IndexOutOfBoundsException {
        if (cardsTable.isEmpty()) {
            throw new IndexOutOfBoundsException("No hay cartas en la mesa.");
        }
        return this.cardsTable.get(this.cardsTable.size()-1);
    }


    public ArrayList<Card> getCardsTable() {
        return cardsTable;
    }
}
