package org.example.eiscuno.model.table;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

/**
 * Represents the table in the Uno game where cards are played.
 */
public class Table {
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


    /*
    Este metodo esta pensado para hacer las validaciones de si una carta se puede jugar o no
    dependiendo de la carta que esta en la mesa
     */
    public boolean isValidPlay(Card card) {
        /*
        Aqui usamos un operador ternario ya que al inicio cuando la mesa esta vacia produciria un error el querer obtener la carta en la mesa
         */
        Card currentCard = this.getCardsTable().isEmpty() ? null : getCurrentCardOnTheTable() ;

        /*
        Aqui digo que si la mesa esta vacia
        o que si la carta en la mesa es de color negro pues que ponga lo que quiera
        */
        if( this.getCardsTable().isEmpty() || currentCard.getColor().equals("BLACK")) {
            this.addCardOnTheTable(card);
            return true;
        }
        /*
        Aqui se dice que si el color de la carta en la mesa es igual a la carta que se quiere poner
        o que si el valor de la carta en la mesa es igual al valor de la carta que se quiere poner pues que lo deje
        o la otra es que si la carta que se quiere poner es de color negro pues que lo deje
        */
        else if(currentCard.getColor().equals(card.getColor())  || currentCard.getValue().equals(card.getValue()) || card.getColor().equals("BLACK")) {
            this.addCardOnTheTable(card);
            return true;
        }

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
