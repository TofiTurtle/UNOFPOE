package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a wild or special Uno card (e.g., WILD, SKIP, etc.).
 */
public class WildCard extends Card {

    /**
     * Constructs a WildCard with the specified image URL, value, and color.
     *
     * @param url   the URL of the card image
     * @param value the value of the card
     * @param color the color of the card
     */
    public WildCard(String url, String value, String color) {
        super(url, value, color);
    }

    /**
     * Indicates that this card is special.
     *
     * @return true always
     */
    @Override
    public boolean isSpecial() {
        return true;
    }
}
