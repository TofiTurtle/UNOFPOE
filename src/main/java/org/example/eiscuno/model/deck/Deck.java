package org.example.eiscuno.model.deck;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.unoenum.EISCUnoEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/**
 * Represents a deck of Uno cards used in the game.
 * Manages both the main deck and an auxiliary deck for discarded or played cards.
 */
public class Deck implements Serializable {
    private Stack<Card> deckOfCards;
    private Stack<Card> AuxdeckOfCards;

    /**
     * Constructs a new {@code Deck} and initializes it with a set of cards.
     */
    public Deck() {
        deckOfCards = new Stack<>();
        AuxdeckOfCards = new Stack<>();
        initializeDeck();
    }

    /**
     * Initializes the main deck by generating cards based on the {@code EISCUnoEnum} values.
     * Uses {@code CardFactory} to create appropriate card instances.
     */
    public void initializeDeck() {
        for (EISCUnoEnum cardEnum : EISCUnoEnum.values()) {
            if (cardEnum.name().startsWith("GREEN_") ||
                    cardEnum.name().startsWith("YELLOW_") ||
                    cardEnum.name().startsWith("BLUE_") ||
                    cardEnum.name().startsWith("RED_") ||
                    cardEnum.name().startsWith("SKIP") ||
                    cardEnum.name().startsWith("RESERVE") ||
                    cardEnum.name().startsWith("TWO_WILD_DRAW") ||
                    cardEnum.name().equals("FOUR_WILD_DRAW") ||
                    cardEnum.name().equals("WILD")) {

                Card card = CardFactory.createCard(cardEnum);
                deckOfCards.push(card);
            }
        }
        Collections.shuffle(deckOfCards);
    }

    /**
     * Draws the top card from the main deck.
     *
     * @return the card drawn from the top of the deck
     * @throws IllegalStateException if the main deck is empty
     */
    public Card takeCard() {
        if (deckOfCards.isEmpty()) {
            throw new IllegalStateException("No more cards in the deck.");
        }
        return deckOfCards.pop();
    }

    /**
     * Checks if the main deck is empty.
     *
     * @return {@code true} if the main deck is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return deckOfCards.isEmpty();
    }

    /**
     * Adds a card back to the main deck and reshuffles the deck.
     *
     * @param card the card to be added to the deck
     */
    public void addCardToDeck(Card card) {
        deckOfCards.push(card);
        Collections.shuffle(deckOfCards);
    }

    /**
     * Retrieves all cards currently in the main deck.
     *
     * @return an {@code ArrayList} of cards in the main deck
     */
    public ArrayList<Card> getCards() {
        return new ArrayList<>(deckOfCards);
    }

    /**
     * Retrieves all cards in the auxiliary deck (played or discarded cards).
     *
     * @return an {@code ArrayList} of cards in the auxiliary deck
     */
    public ArrayList<Card> getAuxCards() {
        return new ArrayList<>(AuxdeckOfCards);
    }

    /**
     * Returns the number of cards in the auxiliary deck.
     *
     * @return the size of the auxiliary deck
     */
    public int getAuxDeckSize() {
        return AuxdeckOfCards.size();
    }

    /**
     * Returns the number of cards in the main deck.
     *
     * @return the size of the main deck
     */
    public int getDeckSize() {
        return deckOfCards.size();
    }

    /**
     * Pushes a card onto the auxiliary deck (e.g., after being played).
     *
     * @param card the card to be pushed onto the auxiliary deck
     */
    public void PushToAuxDeck(Card card) {
        AuxdeckOfCards.add(card);
    }

    /**
     * Refills the main deck with all cards from the auxiliary deck and shuffles them.
     * Used when the main deck runs out of cards.
     */
    public void RefillCards() {
        while (!AuxdeckOfCards.isEmpty()) {
            deckOfCards.push(AuxdeckOfCards.pop());
        }
        Collections.shuffle(deckOfCards);
    }
}
