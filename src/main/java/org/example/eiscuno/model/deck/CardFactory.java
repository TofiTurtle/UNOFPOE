package org.example.eiscuno.model.deck;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.card.NormalCard;
import org.example.eiscuno.model.card.WildCard;
import org.example.eiscuno.model.unoenum.EISCUnoEnum;

/**
 * Factory class responsible for creating Uno card objects based on enum definitions.
 * This class abstracts the logic for distinguishing between normal and special cards.
 */
public class CardFactory {

    /**
     * Creates a {@link Card} instance based on the specified {@link EISCUnoEnum} value.
     * Depending on the card value, it creates either a {@link NormalCard} or a {@link WildCard}.
     *
     * @param cardEnum the enum representing the card type and its metadata
     * @return a new instance of {@code Card}, either normal or special
     */
    public static Card createCard(EISCUnoEnum cardEnum) {
        String name = cardEnum.name();
        String url = cardEnum.getFilePath();
        String value = getCardValue(name);
        String color = getCardColor(name);

        if (isSpecialCard(value)) {
            return new WildCard(url, value, color);
        } else {
            return new NormalCard(url, value, color);
        }
    }

    /**
     * Determines whether a card value corresponds to a special card.
     *
     * @param value the card value to check
     * @return {@code true} if the card is special (e.g., SKIP, WILD, etc.), {@code false} otherwise
     */
    private static boolean isSpecialCard(String value) {
        return value.startsWith("SKIP") || value.startsWith("WILD") ||
                value.startsWith("TWO_WILD") || value.startsWith("FOUR_WILD") ||
                value.startsWith("RESERVE");
    }

    /**
     * Extracts the card value from the enum name.
     *
     * @param name the name of the enum constant
     * @return a string representing the card value (e.g., "5", "SKIP", "WILD")
     */
    private static String getCardValue(String name) {
        if (name.endsWith("0")) return "0";
        if (name.endsWith("1")) return "1";
        if (name.endsWith("2")) return "2";
        if (name.endsWith("3")) return "3";
        if (name.endsWith("4")) return "4";
        if (name.endsWith("5")) return "5";
        if (name.endsWith("6")) return "6";
        if (name.endsWith("7")) return "7";
        if (name.endsWith("8")) return "8";
        if (name.endsWith("9")) return "9";
        if (name.startsWith("SKIP")) return "SKIP";
        if (name.startsWith("WILD")) return "WILD";
        if (name.startsWith("TWO_WILD")) return "TWO_WILD";
        if (name.startsWith("FOUR_WILD")) return "FOUR_WILD";
        if (name.startsWith("RESERVE")) return "RESERVE";
        return null;
    }

    /**
     * Extracts the card color from the enum name.
     *
     * @param name the name of the enum constant
     * @return a string representing the card color (e.g., "RED", "GREEN", "BLACK")
     */
    private static String getCardColor(String name) {
        if (name.startsWith("GREEN") || name.endsWith("GREEN")) return "GREEN";
        if (name.startsWith("YELLOW") || name.endsWith("YELLOW")) return "YELLOW";
        if (name.startsWith("BLUE") || name.endsWith("BLUE")) return "BLUE";
        if (name.startsWith("RED") || name.endsWith("RED")) return "RED";
        if (name.endsWith("DRAW") || name.startsWith("WILD")) return "BLACK";
        return null;
    }
}
