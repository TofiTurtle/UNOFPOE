package org.example.eiscuno.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private boolean initialValidCard = false;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        initVariables();

        //Bucle para prevenir que se pongan cartas especiales como carta inicial de partida
        while(!initialValidCard) //mientras que NO sea una carta inicial valida, se repetira...
        {
            Card firstCard = deck.takeCard(); //tomamos la carta arriba de la pila
            if(!firstCard.isSpecial()) //Si NO es especial
            {
                initialValidCard = true; //"Desbloqueamos" el ciclo while para que siga el programa
                table.addCardOnTheTable(firstCard); //ponemos la carta en la table
                tableImageView.setImage(firstCard.getImage()); //ponemos la IMAGEN de esta
            }else //si SI es especial
            {
                deck.addCardToDeck(firstCard); //llamamos al metodo addCardtodeck (de Clase deck)
            }
        }
        //el resto de codigo se ejecuta ordinariamente...

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
                    deactivateEmptyDeck();
                    // gameUno.playCard(card); ya no se usa porque en el metodo ya se agregan
                    tableImageView.setImage(card.getImage());
                    humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                    //Condicional para que si el jugador usa el reserve o el skip, no se le deshabilite el deck
                    //y este pueda seguir tomando cartas
                    if(card.getValue().equals("SKIP") || card.getValue().equals("RESERVE")) {
                        buttonDeck.setDisable(false);
                    }else{
                        buttonDeck.setDisable(true);
                    }

                    /*
                    hacemos la verificacion de si la carta jugada es un comodin, lo hacemos antes de usar el metodo
                    setHasPlayerPlayed para que la maquina no pueda jugar aun, mientras hacemos las validaciones y demas
                     */
                    if(card.isSpecial()) { //si ES especial
                        handleSpecialCard(card,machinePlayer); //dependiendo del caso, aplique efecto
                    }
                    else { //si no es especial... (normal )
                        threadPlayMachine.setHasPlayerPlayed(true); //dele turno a la machin
                    }
                    printCardsHumanPlayer();

                    //esto iria con un condicional y pondriamos una alerta o algo asi
                    gameUno.isGameOver();


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
    public void handleSpecialCard(Card card, Player targetPlayer) {
        List<String> options = Arrays.asList("Rojo", "Verde", "Azul","Amarillo");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Rojo", options);
        dialog.setTitle("Seleccionar color");
        dialog.setHeaderText("Escoge un color");
        dialog.setContentText("Colores disponibles:");


        switch (card.getValue())
        {
                case "SKIP":
                    System.out.println("SKIP USED!");
                    if (targetPlayer == machinePlayer) { //"si lo tiro el jugador"
                        threadPlayMachine.setHasPlayerPlayed(false); //sigue jugando el jugador, se skipeo machin
                    }else{ //"sino (si lo tiro la machine)"
                        threadPlayMachine.setHasPlayerPlayed(true); //sigue jugando la machin, skipea player
                    }
                    break;
                case "RESERVE":
                    System.out.println("RESERVE USED!");
                    if (targetPlayer == machinePlayer) {
                        threadPlayMachine.setHasPlayerPlayed(false); //sigue jugando el jugador, se skipeo machin
                    }else{
                        threadPlayMachine.setHasPlayerPlayed(true); //sigue jugando la machin, skipea player
                    }
                    break;
                case "WILD":
                    /*Aqui hace falta hacer una implementacion de crear un menu interactivo para
                    escoger el color que se escoge con la carta WILD.
                    De momento solo "pasa" el turno", no tiene efecto alguno, solo que se puede tirar en
                    cualquier momento.
                    * */
                    System.out.println("WILD USED!");
                    if (targetPlayer == machinePlayer) {
                        printCardsHumanPlayer();

                        //logica para cambiar el color del juego
                        Optional<String> result = dialog.showAndWait();
                        result.ifPresent(color -> {
                            System.out.println("Color seleccionado: " + color);
                            color = translateColor(color);
                            //el jugador escoge un color, entonces la carda se le setea ese color para que ese sea el color valido para continuar jugando
                            card.setColor(color);
                        });

                        threadPlayMachine.setHasPlayerPlayed(true); //se le da a la maquina
                    }else{
                        threadPlayMachine.setHasPlayerPlayed(false); //se le da el turno al jugador
                    }
                    break;
                case "TWO_WILD":
                    System.out.println("TWO_WILD USED! +2");
                    if (targetPlayer == machinePlayer) { //si el jugador tiro el +2
                        gameUno.eatCard(machinePlayer, 2); //la machin come 2
                        threadPlayMachine.setHasPlayerPlayed(true); //el turno pasa a ser de ella
                    }else{ //si lo tiro la machin
                        gameUno.eatCard(humanPlayer, 2); //el jugador se come 2
                        threadPlayMachine.setHasPlayerPlayed(false); //el turno ahora es del player
                    }
                    break;
                case "FOUR_WILD":
                    System.out.println("FOUR_WILD USED! +4");
                    if (targetPlayer == machinePlayer) { //si el jugador tiro el +4
                        gameUno.eatCard(machinePlayer, 4); //la machin come 4
                        printCardsHumanPlayer();

                        //logica para cambiar el color del juego
                        Optional<String> result = dialog.showAndWait();
                        result.ifPresent(color -> {
                            System.out.println("Color seleccionado: " + color);
                            color = translateColor(color);
                            //el jugador escoge un color, entonces la carda se le setea ese color para que ese sea el color valido para continuar jugando
                            card.setColor(color);
                        });

                        threadPlayMachine.setHasPlayerPlayed(true); //el turno pasa a ser de ella
                    }else{ //si lo tiro la machin
                        gameUno.eatCard(humanPlayer, 4); //el jugador se come 4
                        threadPlayMachine.setHasPlayerPlayed(false); //el turno ahora es del player
                    }
                    break;
                default:
                    System.out.println("Error, caso NO manejado!");

            }
    }

    public void deactivateEmptyDeck() {
        if(deck.isEmpty()) {
            buttonDeck.setDisable(true);
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

    public String translateColor(String toTranslate) {
        return switch (toTranslate) {
            case "Amarillo" -> "YELLOW";
            case "Azul" -> "BLUE";
            case "Rojo" -> "RED";
            case "Verde" -> "GREEN";
            default -> null;
        };
    }

}
