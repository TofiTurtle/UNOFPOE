package org.example.eiscuno.model.deck;

import org.example.eiscuno.model.card.NormalCard;
import org.example.eiscuno.model.card.WildCard;
import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Stack;

/**
 * Represents a deck of Uno cards.
 */
public class Deck {
    private Stack<Card> deckOfCards;
    private Stack<Card> AuxdeckOfCards;

    /**
     * Constructs a new deck of Uno cards and initializes it.
     */
    public Deck() {
        deckOfCards = new Stack<>();
        AuxdeckOfCards = new Stack<>();
        initializeDeck();
    }

    /**
     * Initializes the deck with cards based on the EISCUnoEnum values.
     */
    private void initializeDeck() {
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
                String value = getCardValue(cardEnum.name());
                String color = getCardColor(cardEnum.name());
                String url = cardEnum.getFilePath();

                Card card;

                if (value.startsWith("SKIP") || value.startsWith("WILD") || value.startsWith("TWO_WILD")
                        || value.startsWith("FOUR_WILD") || value.startsWith("RESERVE")) {
                    card = new WildCard(url, value, color);
                } else {
                    card = new NormalCard(url, value, color);
                }

                deckOfCards.push(card);
            }
        }
        Collections.shuffle(deckOfCards);
    }

    private String getCardValue(String name) {
        if (name.endsWith("0")){
            return "0";
        } else if (name.endsWith("1")){
            return "1";
        } else if (name.endsWith("2")){
            return "2";
        } else if (name.endsWith("3")){
            return "3";
        } else if (name.endsWith("4")){
            return "4";
        } else if (name.endsWith("5")){
            return "5";
        } else if (name.endsWith("6")){
            return "6";
        } else if (name.endsWith("7")){
            return "7";
        } else if (name.endsWith("8")){
            return "8";
        } else if (name.endsWith("9")) {
            return "9";
        } else if (name.startsWith("SKIP")) {
            return "SKIP";
        } else if (name.startsWith("WILD")) {
            return "WILD";
        } else if (name.startsWith("TWO_WILD")) {
            return "TWO_WILD";
        } else if (name.startsWith("FOUR_WILD")) {
            return "FOUR_WILD";
        } else if (name.startsWith("RESERVE")) {
            return "RESERVE";
        } else {
            return null;
        }

    }

    private String getCardColor(String name){
        if(name.startsWith("GREEN")){
            return "GREEN";
        } else if(name.startsWith("YELLOW")){
            return "YELLOW";
        } else if(name.startsWith("BLUE")){
            return "BLUE";
        } else if(name.startsWith("RED")){
            return "RED";
        } else if (name.endsWith("GREEN")) {
            return "GREEN";
        } else if(name.endsWith("YELLOW")){
            return "YELLOW";
        } else if(name.endsWith("BLUE")){
            return "BLUE";
        } else if(name.endsWith("RED")){
            return "RED";
        } else if(name.endsWith("DRAW")) {
            return "BLACK";
        } else if (name.startsWith("WILD")) {
            return "BLACK";
        } else {
            return null;
        }
    }

    /**
     * Takes a card from the top of the deck.
     *
     * @return the card from the top of the deck
     * @throws IllegalStateException if the deck is empty
     */
    public Card takeCard() {
        if (deckOfCards.isEmpty()) {
            throw new IllegalStateException("No hay más cartas en el mazo.");
        }
        Card auxCard = deckOfCards.pop(); //var temporal auxiliar para ver QUE carta agarra
        //System.out.println("Cogio la carta -> " + auxCard.getColor() + ": " + auxCard.getValue());

        //Verificacion de la cantidad de cartas del mazo
        //System.out.println("CARDS REMAINING ON DECK OF CARDS -> : "+ deckOfCards.size());
        return auxCard;
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return deckOfCards.isEmpty();
    }

    /*Nuevo metodo addCardToDeck se crea para prevenir que la carta inicial sea una carta especial
    Lo que este hace es: lo llamamos cuando detectamos que se tiene una carta especial, entonces
    lo que el hace es tomar la carta y pushearla en el mazo de nuevo. (quedando en la parte superior
    de la pila). Por lo que, para no quedar en un bucle infinito, sencillamente re-barajamos el mazo
    una vez mas.
    ->al estar esto dentro del do while, se hace hasta que la carta inicial sea valida!
     */
    public void addCardToDeck (Card card) {
        deckOfCards.push(card); //lo manda arriba de la pila
        Collections.shuffle(deckOfCards); //lo mezcla
    }

    public ArrayList<Card> getCards() {
        ArrayList<Card> deck = new ArrayList<>();
        deck.addAll(deckOfCards);
        return deck;
    }
    public ArrayList<Card> getAuxCards() {
        ArrayList<Card> auxdeck = new ArrayList<>();
        auxdeck.addAll(AuxdeckOfCards);
        return auxdeck;
    }

    //implementacion para evitar el fin del juego: NUEVOS METODOS

    //metodos getter
    public int getAuxDeckSize() {
        return AuxdeckOfCards.size();
    }
    public int getDeckSize(){
        return deckOfCards.size();
    }
    //este metodo sirve para que la carta (que vendria ser la que SE PONE) se sume a este
    //maso auxiliar, para que almacene cartas usadas
    public void PushToAuxDeck(Card card) {
        AuxdeckOfCards.add(card);
    }
    /*Por ultimo, el metodo refill lo que hace es que cuando se llame, va a vaciar TODOU el deck
    auxiliar, y por cada carta que tire en cada iteracion, se la va a "pasar" al maso principal
    deckOfcards. Por ultimo, este metodo hace un shuffle para que cuando se rellene el mazo
    principal, pues las cartas tomen un nuevo orden, y la partida tome un camino mas interesante*/
    public void RefillCards(){
        while (!AuxdeckOfCards.isEmpty()) { //mientras que TENGA CONTENIDO
            deckOfCards.push(AuxdeckOfCards.pop()); //le pasa las cartas al deck principal
        }
        Collections.shuffle(deckOfCards); //justo despues de rellenar, re-barajemos esto
    }

}
