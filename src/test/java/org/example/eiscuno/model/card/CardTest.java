package org.example.eiscuno.model.card;

import org.junit.jupiter.api.Test;

import javafx.application.Platform;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeAll;
class CardTest {
    
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
    void originalColorShouldStillWork_afterChangingCardColor() {
        String url = "/org/example/eiscuno/cards-uno/3_green.png";
        Card card = new Card(url, "3", "GREEN") {
            @Override
            public boolean isSpecial() {
                return false;
            }
        };
        String originalColor = card.getColor();
        card.setColor("BLUE");
        assertEquals(card.getOriginalColor(), originalColor);
    }
}