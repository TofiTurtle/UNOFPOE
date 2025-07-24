package org.example.eiscuno.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.GameUnoStage;

import java.util.Objects;

/**
 * Controller class for the Uno game.
 */
public class GameUnoController {

    @FXML
    private GridPane gridPaneCardsMachine;

    @FXML
    public GridPane gridPaneCardsPlayer;

    @FXML
    private ImageView tableImageView;

    @FXML
    public Button buttonDeck;

    @FXML
    private Button buttonExit;

    @FXML
    private Button buttonUNO;


    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    public GameUno gameUno;
    private int posInitCardToShow;
    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        initVariables();
        Card firstCard = deck.takeCard();
        table.addCardOnTheTable(firstCard);
        tableImageView.setImage(firstCard.getImage());
        this.gameUno.startGame();
        printCardsHumanPlayer();
        printCardsMachinePlayer();

        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer());
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this.deck, this);
        threadPlayMachine.start();
    }

    /**
     * Initializes the variables for the game.
     */
    private void initVariables() {
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;

    }

    /**
     * Prints the human player's cards on the grid pane.
     */
    private void printCardsHumanPlayer() {
        this.gridPaneCardsPlayer.getChildren().clear();
        Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);

        for (int i = 0; i < currentVisibleCardsHumanPlayer.length; i++) {
            Card card = currentVisibleCardsHumanPlayer[i];
            ImageView cardImageView = card.getCard();

            /*
            * Aqui es donde Player Juega una carta
            * */
            cardImageView.setOnMouseClicked((MouseEvent event) -> {
                if(table.isValidPlay(card) ) {
                    // gameUno.playCard(card); ya no se usa porque en el metodo ya se agregan
                    tableImageView.setImage(card.getImage());
                    buttonDeck.setDisable(true);
                    humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                    /*
                    hacemos la verificacion de si la carta jugada es un comodin, lo hacemos antes de usar el metodo
                    setHasPlayerPlayed para que la maquina no pueda jugar aun, mientras hacemos las validaciones y demas
                     */
                    if(card.isSpecial()) {
                        switch (card.getValue())
                        {
                            case "SKIP":
                                System.out.println("SKIP");
                                threadPlayMachine.setHasPlayerPlayed(false); //sigue jugando el jugador
                                break;
                            case "RESERVE":
                                System.out.println("RESERVE");
                                threadPlayMachine.setHasPlayerPlayed(false); //sigue jugando el jugador
                                break;
                            case "WILD":
                                System.out.println("WILD");
                                threadPlayMachine.setHasPlayerPlayed(true);
                                break;
                            case "TWO_WILD":
                                System.out.println("TWO_WILD");
                                gameUno.eatCard(machinePlayer, 2);
                                threadPlayMachine.setHasPlayerPlayed(true);
                                break;
                            case "FOUR_WILD":
                                System.out.println("FOUR_WILD");
                                gameUno.eatCard(machinePlayer, 4);
                                threadPlayMachine.setHasPlayerPlayed(true);
                                break;
                            default:
                                break;
                        }

                    }
                    else {
                        threadPlayMachine.setHasPlayerPlayed(true);
                    }

                    printCardsHumanPlayer();



                }
            });

            this.gridPaneCardsPlayer.add(cardImageView, i, 0);
        }
    }

    public void printCardsMachinePlayer() {
        this.gridPaneCardsMachine.getChildren().clear();
        Card[] currentVisibleCardsMachinePlayer = this.gameUno.getCurrentVisibleCardsMachinePlayer();

        for (int i = 0; i < currentVisibleCardsMachinePlayer.length; i++) {
            Card card = currentVisibleCardsMachinePlayer[i];
            ImageView cardImageView = card.createCardImageViewBack();
            this.gridPaneCardsMachine.add(cardImageView, i, 0);
        }
    }

    /**
     * Finds the position of a specific card in the human player's hand.
     *
     * @param card the card to find
     * @return the position of the card, or -1 if not found
     */
    private Integer findPosCardsHumanPlayer(Card card) {
        for (int i = 0; i < this.humanPlayer.getCardsPlayer().size(); i++) {
            if (this.humanPlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Handles the "Back" button action to show the previous set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleBack(ActionEvent event) {
        if (this.posInitCardToShow > 0) {
            this.posInitCardToShow--;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the "Next" button action to show the next set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleNext(ActionEvent event) {
        if (this.posInitCardToShow < this.humanPlayer.getCardsPlayer().size() - 4) {
            this.posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the action of taking a card.
     *
     * @param event the action event
     */
    @FXML
    void onHandleTakeCard(ActionEvent event) {
        /*
         Ahora el jugador llama a su metodo de agregar una carta
          y a su vez llama a la baraja para que le muestra la carta del peek y la quite
         */
        humanPlayer.addCard(deck.takeCard());
        buttonDeck.setDisable(true);
        threadPlayMachine.setHasPlayerPlayed(true);
        printCardsHumanPlayer();
    }

    /**
     * Handles the action of saying "Uno".
     *
     * @param event the action event
     */
    @FXML
    void onHandleUno(ActionEvent event) {
        // Implement logic to handle Uno event here
    }

    public int getPosInitCardToShow() {
        return posInitCardToShow;
    }

    //este sera el metodo encargado de manejar los diferentes casos comodin, tambien debe recibir el jugador sobre el que tendra efecto
    /*
    Este metodo es el encargado de manejar los diferentes casos comodin
    Recibe el jugador objtivo, o sea sobre el que van a sugur efecto las cartas de +2,+4,...
    Para manejar los casos donde se quita el turno de el otro jugador
    Esto se eliminara y se movera completamente a la clase gameUno
     */
    /*Nota de Juan: si le hiciste pull o algo, de momento no uso esta funcion en el controller, mñna cambio
    la logica para que quede mas compacto
    *
    * */
    public String handleWildCard(Card card, Player targetPlayer) {
        String valueCard = card.getValue();
        Card auxCard;

        switch (valueCard) {
            case "SKIP":
                System.out.println("Caso manejado, nombre carta: " + valueCard);
                return "SKIP";
            case "WILD":
                System.out.println("Caso manejado, nombre carta: " + valueCard);
                return "WILD";
            case "RESERVE":
                System.out.println("Caso manejado, nombre carta: " + valueCard);
                return "RESERVE";
            case "TWO_WILD":
                System.out.println("Caso manejado, nombre carta: " + valueCard);
                return "TWO_WILD";
            case "FOUR_WILD":
                System.out.println("Caso manejado, nombre carta: " + valueCard);
                return "FOUR_WILD";

            /*
            case "TWO_WILD":
                for (int i = 0; i < 2; i++) {
                    auxCard = deck.takeCard();
                    targetPlayer.addCard(auxCard);
                }
                System.out.println("Caso manejado, nombre carta: " + valueCard);
                return "TWO_WILD";

            case "FOUR_WILD":
                for (int i = 0; i < 4; i++) {
                    auxCard = deck.takeCard();
                    targetPlayer.addCard(auxCard);
                }
                System.out.println("Caso manejado, nombre carta: " + valueCard);
                return "FOUR_WILD";
            */
            default:
                return ("Caso no manejado aún, nombre carta: " + valueCard);
        }
    }

    

    @FXML
    void onHandleExit(ActionEvent event) {
        GameUnoStage.deleteInstance();
    }

    //Getter para los Players
    public Player getMachinePlayer() {
        return machinePlayer;
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

}
