package org.example.eiscuno.model.player;

import org.example.eiscuno.model.card.Card;
import java.util.ArrayList;

/**
 * Adapter class for {@link IPlayer} interface.
 * <p>
 * Provides default empty implementations of player methods to allow concrete player implementations
 * to selectively override only the methods they need. This follows the adapter design pattern.
 * </p>
 */
public class PlayerAdapter implements IPlayer {

    /**
     * {@inheritDoc}
     * <p>
     * Default implementation does nothing with the added card.
     * </p>
     *
     * @param card the card to be added to the player's hand
     */
    @Override
    public void addCard(Card card) {
        // Default implementation does nothing
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default implementation always returns null.
     * </p>
     *
     * @param index the position of the card to retrieve
     * @return always returns null in this default implementation
     */
    @Override
    public Card getCard(int index) {
        // Default implementation returns null
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default implementation returns an empty ArrayList.
     * </p>
     *
     * @return an empty list of cards
     */
    @Override
    public ArrayList<Card> getCardsPlayer() {
        // Default implementation returns empty list
        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default implementation does not remove any card.
     * </p>
     *
     * @param index the position of the card to remove
     */
    @Override
    public void removeCard(int index) {
        // Default implementation does nothing
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default empty implementation of a generic player function.
     * </p>
     */
    @Override
    public void genericFunction() {
        // Default empty implementation
    }
}