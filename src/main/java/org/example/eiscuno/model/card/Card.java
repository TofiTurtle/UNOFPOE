package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.Serializable;

/**
 * Represents a generic card in the Uno game.
 * This class is abstract and serves as the base for specific types of cards.
 */
public abstract class Card implements Serializable {
    protected String url;
    protected String value;
    protected String color;
    protected transient Image image;
    protected transient ImageView cardImageView;
    private final String originalColor;
    private static transient final Image backImage =
            new Image(Card.class.getResource("/org/example/eiscuno/cards-uno/card_uno.png").toExternalForm());

    /**
     * Constructs a Card with the specified image URL, value, and color.
     *
     * @param url   the URL of the card image
     * @param value the value of the card
     * @param color the color of the card
     */
    public Card(String url, String value, String color) {
        this.url = url;
        this.value = value;
        this.color = color;
        this.originalColor = color;
        this.image = new Image(String.valueOf(getClass().getResource(url)));
        this.cardImageView = createCardImageView();
    }

    /**
     * Creates and configures the ImageView for the card.
     *
     * @return the configured ImageView of the card
     */
    protected ImageView createCardImageView() {
        ImageView card = new ImageView(this.image);
        card.setY(16);
        card.setFitHeight(90);
        card.setFitWidth(70);
        return card;
    }

    /**
     * Creates and returns the back view of a card.
     *
     * @return the ImageView showing the card back
     */
    public ImageView createCardImageViewBack() {
        ImageView card = new ImageView(backImage);
        card.setY(16);
        card.setFitHeight(90);
        card.setFitWidth(70);
        return card;
    }

    public void rebuildCardImageView() {
        this.image = new Image(String.valueOf(getClass().getResource(url)));
        this.cardImageView = new ImageView(this.image);
        cardImageView.setY(16);
        cardImageView.setFitHeight(90);
        cardImageView.setFitWidth(70);
    }

    /**
     * Gets the ImageView representation of the card.
     *
     * @return the ImageView of the card
     */
    public ImageView getCard() {
        return cardImageView;
    }

    /**
     * Gets the image of the card.
     *
     * @return the Image of the card
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the color of the card.
     *
     * @param color the new color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets the value of the card.
     *
     * @return the value of the card
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the color of the card.
     *
     * @return the color of the card
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the original color of the card.
     *
     * @return the original color
     */
    public String getOriginalColor() {
        return originalColor;
    }

    /**
     * Indicates whether the card is a special card (e.g., wild, skip).
     *
     * @return true if the card is special, false otherwise
     */
    public abstract boolean isSpecial();

    /**
     * Gets the static back image used for all cards.
     *
     * @return the back image
     */
    public static Image getBackImage() {
        return backImage;
    }
}
