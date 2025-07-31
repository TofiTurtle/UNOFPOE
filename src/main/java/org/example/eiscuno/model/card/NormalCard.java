package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a normal (non-special) Uno card.
 */
public class NormalCard extends Card {

    /**
     * Constructs a NormalCard with the specified image URL, value, and color.
     *
     * @param url   the URL of the card image
     * @param value the value of the card
     * @param color the color of the card
     */
    public NormalCard(String url, String value, String color) {
        super(url, value, color);
    }

    /**
     * Indicates that this card is not special.
     *
     * @return false always
     */
    @Override
    public boolean isSpecial() {
        return false;
    }
}
