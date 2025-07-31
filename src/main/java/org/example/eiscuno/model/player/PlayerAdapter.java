package org.example.eiscuno.model.player;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

/**
 * Adapter class for IPlayer.
 * Provides empty implementations to allow selective overriding.
 */
public class PlayerAdapter implements IPlayer {

    @Override
    public void addCard(Card card) {
        // Default implementation does nothing
    }

    @Override
    public Card getCard(int index) {
        // Default implementation returns null
        return null;
    }

    @Override
    public ArrayList<Card> getCardsPlayer() {
        // Default implementation returns empty list
        return new ArrayList<>();
    }

    @Override
    public void removeCard(int index) {
        // Default implementation does nothing
    }
    @Override
    public void genericFunction(){};
}
