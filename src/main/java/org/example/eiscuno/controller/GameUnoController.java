package org.example.eiscuno.controller;

import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.example.eiscuno.exceptions.PenaltyException;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.player.ThreadSingUNOPlayer;
import org.example.eiscuno.model.saveGame.GameState;
import org.example.eiscuno.model.saveGame.SerializableFileHandler;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.exceptions.PenaltyException;
import org.example.eiscuno.view.StartUnoView;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Controller class for the Uno game.
 */
public class GameUnoController {

    @FXML
    public ImageView imageViewDeck;
    @FXML
    private ImageView imageViewUNO;
    @FXML
    private Label labelAlertMachine;
    @FXML
    public Pane stackPaneCardsMachine;
    @FXML
    public StackPane stackPaneCardsPlayer;
    @FXML
    private ImageView tableImageView;
    @FXML
    public Button buttonDeck;
    @FXML
    private Button buttonExit;
    @FXML
    private Button buttonUNO;
    @FXML
    private ImageView playerImage;
    @FXML
    private Label playerNickname;
    @FXML
    private HBox colorChooserHBox;
    @FXML
    private Button buttonRed;
    @FXML
    private Button buttonGreen;
    @FXML
    private Button buttonBlue;
    @FXML
    private Button buttonYellow;

    private Consumer<String> onColorPicked;
    private Queue<String> alertQueue = new LinkedList<>();
    private boolean isAlertShowing = false;

    public Player humanPlayer;
    private Player machinePlayer;
    public Deck deck;
    private Table table;
    public GameUno gameUno;
    private int posInitCardToShow;
    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;
    private boolean initialValidCard = false;
    public boolean playerSaidUNO = false;
    public boolean machineSaidUNO = false;
    public boolean unoCheckMachineStarted = false;
    public boolean unoCheckStarted = false;
    private Map<Card, ImageView> machineCardViews = new HashMap<>();
    private String playerName;
    private String currentImage;

    /** Utility for saving and loading game state. */
    private SerializableFileHandler serializableFileHandler;

    /** Serializable object that stores the current game state. */
    private GameState gameState;

    /**
     * Initializes the controller.
     * This method always runs when the FXML is loaded.
     */
    @FXML
    public void initialize() {
        labelAlertMachine.setText("");
        serializableFileHandler = new SerializableFileHandler();
        // Event handling for color selection (wild cards).
        buttonRed.setOnAction(e -> handleColorPick("RED"));
        buttonGreen.setOnAction(e -> handleColorPick("GREEN"));
        buttonBlue.setOnAction(e -> handleColorPick("BLUE"));
        buttonYellow.setOnAction(e -> handleColorPick("YELLOW"));

    }

    /**
     * Starts a completely new game session.
     * Initializes deck, table, players, and game threads.
     * Ensures the first card is not a special card before placing it on the table.
     */
    public void setupNewGame(){
        System.out.println("Cargando una NUEVA PARTIDA");
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;

        // Ensure the initial card is valid (not a special card).
        while(!initialValidCard) {
            Card firstCard = deck.takeCard();
            if(!firstCard.isSpecial())
            {
                initialValidCard = true;
                table.addCardOnTheTable(firstCard);
                tableImageView.setImage(firstCard.getImage());
                deck.PushToAuxDeck(firstCard);
                System.out.println("carta inicial guardada!! -> CANTIDAD DE CARTAS EN EL MAZO AUXILIAR: "+ deck.getAuxDeckSize());
            }else
            {
                deck.addCardToDeck(firstCard);
            }
            labelAlertMachine.textProperty().addListener((obs, oldText, newText) -> {
                if (newText == null || newText.trim().isEmpty()) {
                    labelAlertMachine.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                } else {
                    labelAlertMachine.setStyle(
                            "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;" +
                                    "-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 10;" +
                                    "-fx-padding: 6 12 6 12;"
                    );
                }
            });
        }
        this.gameUno.startGame();
        printCardsHumanPlayer();
        printCardsMachinePlayer();

        // Start machine threads
        threadSingUNOMachine = new ThreadSingUNOMachine(humanPlayer.getCardsPlayer(), this);
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this.deck ,this, machineCardViews, stackPaneCardsMachine);
        threadPlayMachine.start();

        saveGame();
    }

    /**
     * Loads a previously saved game session.
     * Restores players, deck, table, and game threads.
     * Rebuilds card images after deserialization.
     */
    public void loadSavedGame() {
        System.out.println("cargando una PARTIDA YA INICIADA CONTINUADA");
        this.humanPlayer = gameState.getHumanPlayer();
        this.machinePlayer = gameState.getMachinePlayer();
        this.deck = gameState.getDeck();
        this.table = gameState.getTable();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);

        // Rebuild visual components of cards after deserialization.
        for (Card c : humanPlayer.getCardsPlayer()) {
            c.rebuildCardImageView();
        }
        for (Card c : machinePlayer.getCardsPlayer()) {
            c.rebuildCardImageView();
        }
        for (Card c : deck.getCards()) {
            c.rebuildCardImageView();
        }
        for (Card c : deck.getAuxCards()) {
            c.rebuildCardImageView();
        }

        // Restore central card on the table.
        table.addCardOnTheTable(table.getCurrentCardOnTheTable()); //ponemos la carta en la table
        tableImageView.setImage(table.getCurrentCardOnTheTable().getImage()); //ponemos la IMAGEN de esta
        //imprimimos las cartas de los jugadores
        printCardsHumanPlayer();
        printCardsMachinePlayer();

        // Restart threads with restored state.
        threadSingUNOMachine = new ThreadSingUNOMachine(humanPlayer.getCardsPlayer(), this);
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this.deck ,this, machineCardViews, stackPaneCardsMachine);
        threadPlayMachine.start();

        saveGame();
    }

    /**
     * Initializes player settings such as name, avatar image, and game state.
     * @param playerName chosen nickname of the player
     * @param currentImage avatar image path
     * @param gameState saved state to load, or null for a new game
     */
    public void initPlayer(String playerName, String currentImage, GameState gameState) {
        this.playerName = playerName;
        this.currentImage = currentImage;
        this.gameState = gameState;

        if(this.gameState==null){
            setupNewGame();
        }else{
            loadSavedGame();
        }
    }

    /** Sets the avatar image of the player. */
    public void setPlayerImage() {
        playerImage.setImage(new Image(getClass().getResourceAsStream(currentImage)));
    }
    /** Sets the nickname label of the player. */
    public void setPlayerNickname() {
        playerNickname.setText(playerName);
    }

    /**
     * Saves the current game state to a serialized file.
     * Data includes players, deck, and table status.
     */
    public void saveGame(){
        //guardamos la partida con los objetos del juego:
        GameState gameState = new GameState(humanPlayer, machinePlayer, deck, table);
        //serializamos
        serializableFileHandler.serialize("game_data.ser", gameState);
        //verificacion
        System.out.println("Si se guardo manito, calma! :)))");
    }


    /**
     * Renders the human player's cards on the screen and attaches
     * interaction logic (hover effect, card play, animations).
     * Also handles game-over conditions.
     */
    public void printCardsHumanPlayer() {
        this.stackPaneCardsPlayer.getChildren().clear();
        List<Card> cards = this.humanPlayer.getCardsPlayer();

        // Check for game over before rendering cards
        if(gameUno.isGameOver()==1 || gameUno.isGameOver()==2){
            threadPlayMachine.stop();
            stackPaneCardsPlayer.setDisable(true);
            deactivateEmptyDeck();

            if(gameUno.isGameOver()==1){
                showGameAlert("*-*-*- GANO LA MAQUINA... *-*-*-");
            }else{
                showGameAlert("*-*-*- GANO EL JUGADOR, FELICIDADES! *-*-*-");

            }
        }else{
            int offset = Math.max(20, 300 / cards.size());
            int totalWidth = (cards.size() - 1) * offset;
            int startOffset = -totalWidth / 2;

            for (int i = 0; i < cards.size(); i++) {

                Card card = cards.get(i);
                ImageView cardImageView = card.getCard();

                // Highlight rectangle for hover effect
                Rectangle highlight = new Rectangle(70, 90);
                highlight.setFill(null);
                highlight.setStroke(Color.LIMEGREEN);
                highlight.setStrokeWidth(15);
                highlight.setArcWidth(10);
                highlight.setArcHeight(10);
                highlight.setVisible(false);

                cardImageView.setStyle("-fx-effect: dropshadow(gaussian, transparent, 0, 0, 0, 0);");

                // Hover effect
                cardImageView.setOnMouseEntered(e -> {
                    highlight.setVisible(true);
                    cardImageView.setScaleX(1.05);
                    cardImageView.setScaleY(1.05);
                    cardImageView.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);");
                });
                cardImageView.setOnMouseExited(e -> {
                    highlight.setVisible(false);
                    cardImageView.setScaleX(1.0);
                    cardImageView.setScaleY(1.0);
                    cardImageView.setStyle("-fx-effect: dropshadow(gaussian, transparent, 0, 0, 0, 0);");
                });


                /**
                 * When clicked, the player attempts to play a card.
                 * Validates the move, animates the play, updates game state,
                 * and transfers turn to the machine if appropriate.
                 */
                cardImageView.setOnMouseClicked((MouseEvent event) -> {
                    showGameAlert("");
                    if(table.isValidPlay(card) ) {
                        Animations.playCardAnimation(card, cardImageView, tableImageView, () -> {
                            humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                            deck.PushToAuxDeck(card);
                            saveGame();
                            if (humanPlayer.getCardsPlayer().size() == 1 && !unoCheckStarted) {
                                unoCheckStarted = true;
                                playerSaidUNO = false; // ← importante reiniciar bandera
                            }


                            // Skip/Reserve exceptions: allow drawing
                            if (card.getValue().equals("SKIP") || card.getValue().equals("RESERVE")) {
                                imageViewDeck.setOpacity(1);
                                buttonDeck.setDisable(false);
                            } else {
                                imageViewDeck.setOpacity(0.5);
                                buttonDeck.setDisable(true);
                            }

                            if (card.isSpecial()) {
                                Platform.runLater(() -> handleSpecialCard(card, machinePlayer)); //dependiendo del caso, aplique efecto, Platform para que
                                saveGame();
                            } else {
                                threadPlayMachine.setHasPlayerPlayed(true); //dele turno a la machin
                            }
                            printCardsHumanPlayer();
                        });
                    }
                });
                // Stack highlight + card, without blocking clicks
                StackPane container = new StackPane();
                container.getChildren().addAll(highlight, cardImageView);
                container.setPickOnBounds(false);
                container.setTranslateX(startOffset + i * offset);
                this.stackPaneCardsPlayer.getChildren().add(container);
            }
        };


    }


    /**
     * Starts monitoring when a player or machine has exactly one card left.
     * If "MACHINE", waits for the human player to accuse before
     * the machine declares UNO.
     *
     * @param who string that represents who has one card left.
     *            Expected values: "PLAYER" or "MACHINE".
     */
    private void checkUNO(String who) {
        if (who.equals("MACHINE")) {
            unoCheckMachineStarted = true;
            machineSaidUNO = false;
            System.out.println("La máquina tiene solo una carta. Esperando si el jugador le canta...");

            ThreadSingUNOPlayer threadSingUNOPlayer = new ThreadSingUNOPlayer(this, machinePlayer, machinePlayer.getCardsPlayer(), deck);
            threadSingUNOPlayer.start();
        }
    }


    /**
     * Renders the machine's cards as back-faced images.
     * Maintains references to the visual representations for animations.
     * Also manages UNO monitoring when machine reaches one card.
     */
    public void printCardsMachinePlayer() {
        this.stackPaneCardsMachine.getChildren().clear();
        this.machineCardViews.clear();

        List<Card> cards = this.machinePlayer.getCardsPlayer();
        int numCards = cards.size();
        if (numCards == 0) return;

        int offset = Math.max(20, 300 / numCards);
        int totalWidth = (numCards - 1) * offset;
        int startOffset = -totalWidth / 2;


        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            ImageView cardImageView = card.createCardImageViewBack(); //Reverso
            cardImageView.setLayoutX(150 + i * offset);
            cardImageView.setLayoutY(0);

            stackPaneCardsMachine.getChildren().add(cardImageView);
            machineCardViews.put(card, cardImageView);
        }

        // UNO state reset if machine no longer has exactly one card
        if (machinePlayer.getCardsPlayer().size() != 1) {
            unoCheckMachineStarted = false;
            machineSaidUNO = false;
        }

        // If machine reaches exactly one card, start UNO monitoring
        if (machinePlayer.getCardsPlayer().size() == 1 && !unoCheckMachineStarted) {
            unoCheckMachineStarted = true;
            checkUNO("MACHINE");
        }
    }

    /**
     * Finds the position of a specific card in the human player's hand.
     *
     * @param card the card to locate
     * @return the index of the card in the player's hand, or -1 if not found
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
     * Handles the action when the player chooses to draw a card from the deck.
     * Includes deck refill logic if the deck is running low.
     *
     * @param event the action event triggered by the button
     */
    @FXML
    void onHandleTakeCard(ActionEvent event) {
        buttonDeck.setDisable(true);
        showGameAlert("");
        // Refill deck if necessary
        if(deck.getDeckSize()<=4) {
            System.out.println("Mazo vacio ----> RELLENANDO");
            deck.RefillCards();

            Image cardBackImage = new Image("/org/example/eiscuno/cards-uno/card_uno.png");
            Animations.animateCardFromDeck(
                    cardBackImage,
                    imageViewDeck,
                    stackPaneCardsPlayer,
                    false,
                    () -> {
                        humanPlayer.addCard(deck.takeCard());
                        saveGame();
                        imageViewDeck.setOpacity(0.5);
                        buttonDeck.setDisable(true);
                        threadPlayMachine.setHasPlayerPlayed(true);
                        printCardsHumanPlayer();
                    }
            );
        } else {
            Image cardBackImage = new Image("/org/example/eiscuno/cards-uno/card_uno.png");
            Animations.animateCardFromDeck(
                    cardBackImage,
                    imageViewDeck,
                    stackPaneCardsPlayer,
                    false,
                    () -> {
                        humanPlayer.addCard(deck.takeCard());
                        saveGame();
                        imageViewDeck.setOpacity(0.5);
                        threadPlayMachine.setHasPlayerPlayed(true);
                        printCardsHumanPlayer();
                    }
            );
        }
    }

    /**
     * Handles the action of saying "UNO".
     * Covers both scenarios:
     *  - When the human player has one card left and presses the button on time.
     *  - When the human player accuses the machine of not declaring UNO.
     * If none of these conditions apply, the action is invalid.
     *
     * @param event the action event triggered by the UNO button
     */
    @FXML
    void onHandleUno(ActionEvent event) {
        // Case 1: Human has one card and is in UNO check
        if (humanPlayer.getCardsPlayer().size() == 1 && unoCheckStarted) {
            playerSaidUNO = true;
            System.out.println("El jugador presionó el botón UNO a tiempo.");

            // Case 2: Human accuses the machine for not declaring UNO
        } else if (machinePlayer.getCardsPlayer().size() == 1 && unoCheckMachineStarted && !machineSaidUNO) {
            System.out.println("El jugador acusó a la máquina por no decir UNO a tiempo.");

            machinePlayer.addCard(deck.takeCard());
            saveGame();
            printCardsMachinePlayer();

            Animations.animateCardFromDeck(
                    Card.getBackImage(),
                    imageViewDeck,
                    stackPaneCardsMachine,
                    true,
                    () -> printCardsMachinePlayer()
            );

            unoCheckMachineStarted = false;
            machineSaidUNO = false;
            showGameAlert("¡Acusación exitosa!\nLa máquina no dijo UNO a tiempo y ha sido penalizada.");

            // Invalid case
        } else {
            showGameAlert("No puedes decir UNO ahora.");
        }
    }

    /**
     * Handles the logic for special cards (Skip, Reserve, Wild, +2, +4).
     * Applies the effect to the target player, including animations and turn management.
     *
     * @param card the special card played
     * @param targetPlayer the player affected by the card
     */
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
                    if (targetPlayer == machinePlayer) {
                        threadPlayMachine.setHasPlayerPlayed(false);
                    }else{
                        showGameAlert("La maquina vuelve a tirar");
                        threadPlayMachine.setHasPlayerPlayed(true);
                        System.out.println("El turno : " + threadPlayMachine.getHasPlayerPlay());
                    }
                    break;
                case "RESERVE":
                    System.out.println("RESERVE USED!");
                    if (targetPlayer == machinePlayer) {
                        threadPlayMachine.setHasPlayerPlayed(false);
                    }else{
                        showGameAlert("La maquina vuelve a tirar");
                        threadPlayMachine.setHasPlayerPlayed(true);
                        System.out.println("El turno : " + threadPlayMachine.getHasPlayerPlay());
                    }
                    break;
                case "WILD":
                    System.out.println("WILD USED!");
                    if (targetPlayer == machinePlayer) {
                        printCardsHumanPlayer();
                        showColorChooser("Elige un color para continuar", selectedColor -> {
                            card.setColor(translateColor(selectedColor));
                            threadPlayMachine.setHasPlayerPlayed(true);
                        });
                    } else {
                        String color = getRandomColor(options);
                        card.setColor(translateColor(color));
                        System.out.println("Color escogido: " + color);
                        showGameAlert("La máquina escogió el color: " + color);
                        imageViewDeck.setOpacity(1);
                        buttonDeck.setDisable(false);
                        threadPlayMachine.setHasPlayerPlayed(false);
                    }
                    break;
                case "TWO_WILD":
                    System.out.println("TWO_WILD USED! +2");
                    if (targetPlayer == machinePlayer) {
                        Animations.animateEatCards(machinePlayer, 2, true, gameUno, this);
                        showGameAlert("La maquina comio 2 cartas");
                        threadPlayMachine.setHasPlayerPlayed(true);
                    }else{
                        Animations.animateEatCards(humanPlayer, 2, false, gameUno, this);
                        imageViewDeck.setOpacity(1);
                        buttonDeck.setDisable(false);
                        threadPlayMachine.setHasPlayerPlayed(false);
                        System.out.println("El turno : " + threadPlayMachine.getHasPlayerPlay());
                    }
                    break;
            case "FOUR_WILD":
                System.out.println("FOUR_WILD USED! +4");
                if (targetPlayer == machinePlayer) {
                    Animations.animateEatCards(machinePlayer, 4, true, gameUno, this);
                    showGameAlert("La máquina comió 4 cartas");
                    showColorChooser("Elige un color para continuar", selectedColor -> {
                        card.setColor(translateColor(selectedColor));
                        threadPlayMachine.setHasPlayerPlayed(true);
                    });
                } else {
                    Animations.animateEatCards(humanPlayer, 4, false, gameUno, this);
                    String color = getRandomColor(options);
                    card.setColor(translateColor(color));
                    showGameAlert("La máquina escogió el color: " + color);
                    imageViewDeck.setOpacity(1);
                    buttonDeck.setDisable(false);
                    threadPlayMachine.setHasPlayerPlayed(false);
                }
                break;
            default:
                    System.out.println("Error, caso NO manejado!");

            }
    }

    /**
     * Deactivates the deck if empty by disabling UI elements.
     */
    public void deactivateEmptyDeck() {
        if(deck.isEmpty()) {
            imageViewDeck.setOpacity(0.5);
            buttonDeck.setDisable(true);
        }
    }

    /**
     * Handles the exit action. Closes the current game stage
     * and redirects the user to the main menu instead of quitting the app.
     *
     * @param event the action event
     * @throws IOException if the menu view cannot be loaded
     */
    @FXML
    void onHandleExit(ActionEvent event) throws IOException {
        GameUnoStage.deleteInstance();
        //modifico esto para que cuando le des a salir no se salga del todou de la app,
        //mejor que nos mande primero al menu. asi es mas facil hacer pruebas tbn
        StartUnoView.getInstance();
    }

    /** @return the human player object */
    public Player getHumanPlayer() {
        return humanPlayer;
    }

    /**
     * Translates a color name in Spanish to its UNO internal representation.
     *
     * @param toTranslate the color in Spanish
     * @return the color constant (YELLOW, BLUE, RED, GREEN), or null if invalid
     */
    public String translateColor(String toTranslate) {
        return switch (toTranslate) {
            case "Amarillo" -> "YELLOW";
            case "Azul" -> "BLUE";
            case "Rojo" -> "RED";
            case "Verde" -> "GREEN";
            default -> null;
        };
    }

    /** @return label for machine alerts */
    public Label getLabelAlertMachine() {
        return labelAlertMachine;
    }

    public void setUnoCheckMachineStarted(boolean value) {
        this.unoCheckMachineStarted = value;
    }


    public boolean isMachineSaidUNO() {
        return machineSaidUNO;
    }

    public void setMachineSaidUNO(boolean saidUNO) {
        this.machineSaidUNO = saidUNO;
    }

    public Deck getDeck() {
        return this.deck;
    }

    /**
     * Shows a blocking alert dialog with penalty information.
     *
     * @param who player penalized
     * @param message penalty message
     */
    public void showPenaltyAlert(String who, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Penalización");
        alert.setHeaderText("¡" + who + " fue penalizado!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==== Non-blocking game alerts (queued messages) ====
    private boolean isShowingAlert = false;

    /**
     * Shows a non-blocking alert message inside the UI.
     * Messages are queued and shown sequentially for 3 seconds each.
     *
     * @param message the message to display
     */
    public void showGameAlert(String message) {
        Platform.runLater(() -> {
            alertQueue.offer(message);
            processNextAlert();
        });
    }

    /** Processes the next alert in the queue if none is currently being shown. */
    private void processNextAlert() {
        if (isShowingAlert || alertQueue.isEmpty()) {return;}

        isShowingAlert = true;
        String nextMessage = alertQueue.poll(); // Sacamos el siguiente mensaje

        labelAlertMachine.setText(nextMessage);
        labelAlertMachine.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            labelAlertMachine.setText("");
            labelAlertMachine.setVisible(false);
            isShowingAlert = false;
            processNextAlert();
        });
        pause.play();
    }

    /**
     * Handles the logic after a color is picked in the custom chooser.
     *
     * @param color chosen color
     */
    private void handleColorPick(String color) {
        colorChooserHBox.setVisible(false);
        colorChooserHBox.setManaged(false);
        labelAlertMachine.setText("Color seleccionado: " + color);
        if (onColorPicked != null) {
            onColorPicked.accept(color);
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            labelAlertMachine.setText("");
            labelAlertMachine.setVisible(false);
        });
        pause.play();
    }

    /**
     * Picks a random color from the available options.
     *
     * @param options list of color options
     * @return randomly chosen color
     */
    private String getRandomColor(List<String> options) {
        return options.get(new Random().nextInt(options.size()));
    }


    /**
     * Displays a custom non-blocking color chooser using
     * UI buttons instead of blocking dialogs.
     *
     * @param message message to display
     * @param onColorChosen callback executed when a color is selected
     */
    public void showColorChooser(String message, Consumer<String> onColorChosen) {
        Platform.runLater(() -> {
            labelAlertMachine.setText(message);
            labelAlertMachine.setVisible(true);
            colorChooserHBox.setVisible(true);

            for (Node node : colorChooserHBox.getChildren()) {
                if (node instanceof Button) {
                    node.setDisable(false);
                }
            }
            Consumer<String> internalHandler = color -> {
                labelAlertMachine.setText("");
                labelAlertMachine.setVisible(false);
                colorChooserHBox.setVisible(false);
                onColorChosen.accept(color);
            };
            buttonRed.setOnAction(e -> internalHandler.accept("Rojo"));
            buttonGreen.setOnAction(e -> internalHandler.accept("Verde"));
            buttonBlue.setOnAction(e -> internalHandler.accept("Azul"));
            buttonYellow.setOnAction(e -> internalHandler.accept("Amarillo"));
        });
    }
}
