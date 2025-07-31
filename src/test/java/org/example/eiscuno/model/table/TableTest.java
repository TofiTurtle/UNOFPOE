package org.example.eiscuno.model.table;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    @BeforeAll
    static void initJfxToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @Test
    void isValidPlay_shouldAcceptColorMatch_andAddCard() {
        Table table = new Table();
        String url = "/org/example/eiscuno/cards-uno/7_green.png";
        Card start = new Card(url, "7", "GREEN") {
            @Override
            public boolean isSpecial() {
                return false;
            }
        };
        table.addCardOnTheTable(start);

        Card sameColor = new Card("/org/example/eiscuno/cards-uno/2_green.png", "2", "GREEN") {
            @Override
            public boolean isSpecial() {
                return false;
            }
        };
        assertTrue(table.isValidPlay(sameColor));
        assertEquals(sameColor, table.getCurrentCardOnTheTable());
        assertEquals(2, table.getCardsTable().size());
    }

    @Test
    void isValidPlay_shouldRejectInvalid_andNotChangeTable() {
        Table table = new Table();
        String url = "/org/example/eiscuno/cards-uno/5_red.png";
        Card start = new Card(url, "5", "RED") {
            @Override
            public boolean isSpecial() {
                return false;
            }
        };
        table.addCardOnTheTable(start);

        Card invalid = new Card("/org/example/eiscuno/cards-uno/3_blue.png", "3", "BLUE") {
            @Override
            public boolean isSpecial() {
                return false;
            }
        };
        assertFalse(table.isValidPlay(invalid));
        assertEquals(start, table.getCurrentCardOnTheTable());
        assertEquals(1, table.getCardsTable().size());
    }

    @Test
    void getCurrentCardOnTheTable_shouldThrow_ifEmpty() {
        Table table = new Table();
        assertThrows(IndexOutOfBoundsException.class, table::getCurrentCardOnTheTable);
    }

    @Test
    void table_shouldReturnAllCardsPlacedOnIt() {
        Table table = new Table();
        String url = "/org/example/eiscuno/cards-uno/3_green.png";
        Card card = new Card(url, "3", "GREEN") {
            @Override
            public boolean isSpecial() {
                return false;
            }
        };

        for(int i = 0; i < 10; i++) {
            table.addCardOnTheTable(card);
        }

        assertEquals(10,table.getCardsTable().size());
    }
}