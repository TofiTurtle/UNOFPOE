package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a card in the Uno game.
 */
public class Card {
    private String url;
    private String value;
    private String color;
    private Image image;
    private ImageView cardImageView;
    private final String originalColor;

    /**
     * Constructs a Card with the specified image URL and name.
     *
     * @param url the URL of the card image
     * @param value of the card
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
    private ImageView createCardImageView() {
        ImageView card = new ImageView(this.image);
        card.setY(16);
        card.setFitHeight(90);
        card.setFitWidth(70);
        return card;
    }

    public ImageView createCardImageViewBack() {
        ImageView card = new ImageView(getClass().getResource("/org/example/eiscuno/cards-uno/card_uno.png").toExternalForm());
        card.setY(16);
        card.setFitHeight(90);
        card.setFitWidth(70);
        return card;
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

    public void setColor(String color) {
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public String getOriginalColor() {
        return originalColor;
    }


    /*
    Este metodo lo que hara es retornar verdadero si la carta es alguna carta comodin
    si no lo es pues retorna falso
     */
    public boolean isSpecial() {
        return this.getValue().startsWith("SKIP") || this.getValue().startsWith("WILD") || this.getValue().startsWith("TWO_WILD") || this.getValue().startsWith("FOUR_WILD") || this.getValue().startsWith("RESERVE");
    }


}
