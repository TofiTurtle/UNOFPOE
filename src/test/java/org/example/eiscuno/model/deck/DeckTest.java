package org.example.eiscuno.model.deck;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    /*
    Con esto el toolkit de JavaFX queda inicializado y el constructor de Card puede
    cargar imágenes sin lanzar excepciones
     */
    @BeforeAll
    static void initJfxToolkit() throws InterruptedException {
        // Arranca el toolkit de JavaFX una sola vez
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @Test
    void deckSize_shouldBeOneHundredAndEighty() {
        var deck = new Deck();
        deck.initializeDeck();
        assertEquals(108, deck.getDeckSize());
    }

    @Test
    void pushToAuxDeck_and_refill_shouldTransferAllAuxToMain() {
        Deck deck = new Deck();
        deck.initializeDeck();
        int mainBefore = deck.getDeckSize();

        // Extraemos dos cartas y las ponemos en el auxiliar
        Card c1 = deck.takeCard();
        Card c2 = deck.takeCard();
        deck.PushToAuxDeck(c1);
        deck.PushToAuxDeck(c2);
        assertEquals(2, deck.getAuxDeckSize());
        assertEquals(mainBefore - 2, deck.getDeckSize());

        // Ahora rellenamos
        deck.RefillCards();
        assertEquals(0, deck.getAuxDeckSize());
        assertEquals(mainBefore, deck.getDeckSize());
    }

    @Test
    void getCards_shouldReturnSnapshotWithoutModifyingInternal() {
        Deck deck = new Deck();
        deck.initializeDeck();
        List<Card> snapshot = deck.getCards();

        assertEquals(deck.getDeckSize(), snapshot.size());
        // Modificar el snapshot no afecta al deck original
        Card removed = snapshot.remove(0);
        assertEquals(deck.getDeckSize(), snapshot.size() + 1);
        assertTrue(deck.getCards().contains(removed));
    }
}