package org.example.eiscuno.controller;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.example.eiscuno.exceptions.PenaltyException;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.saveGame.GameState;
import org.example.eiscuno.model.saveGame.SerializableFileHandler;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.exceptions.PenaltyException;
import org.example.eiscuno.view.StartUnoView;

import java.io.IOException;
import java.util.*;

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
    //implementacion de archivo plano y serializable
    private String playerName;
    private String currentImage;

    private SerializableFileHandler serializableFileHandler; //vaina para serializar
    private GameState gameState; //objeto que tendra info (guardara las vainas)

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        labelAlertMachine.setText("");
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
                /*En este momento, ya se puso la carta, ya no esta en el deck original
                por lo que la almacenamos en el deckauxiliar para la implementacion!  */
                deck.PushToAuxDeck(firstCard); //llamamos al metodo.
                System.out.println("carta inicial guardada!! -> CANTIDAD DE CARTAS EN EL MAZO AUXILIAR: "+ deck.getAuxDeckSize());
            }else //si SI es especial
            {
                //si se llega aqui, NO se pone la carta, se re-baraja
                deck.addCardToDeck(firstCard); //llamamos al metodo addCardtodeck (de Clase deck)
            }
        }
        //el resto de codigo se ejecuta ordinariamente...

        this.gameUno.startGame();
        printCardsHumanPlayer();
        printCardsMachinePlayer();

        threadSingUNOMachine = new ThreadSingUNOMachine(humanPlayer.getCardsPlayer(), this);
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this.deck ,this, machineCardViews, stackPaneCardsMachine);
        threadPlayMachine.start();

        //implementacion de serializable
        serializableFileHandler = new SerializableFileHandler();


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

    //ESTOS METODOS DE ACA SON PURAMENTE PARA PONERLE LA IMAGEN Y NOMBRE SELECCIONADO AL JUGADOR!!!
    public void initPlayer(String playerName, String currentImage) {
        this.playerName = playerName;
        this.currentImage = currentImage;
        // Lógica para usar esos datos: ponerlos en labels, imágenes, etc.
    }
    //metodo pa ponerle la imagen al jugador
    public void setPlayerImage() {
        playerImage.setImage(new Image(getClass().getResourceAsStream(currentImage)));
    }
    public void setPlayerNickname() {
        playerNickname.setText(playerName);
    }
    private void saveGame(){
        ArrayList<Card> PlayerCards = humanPlayer.getCardsPlayer();
        ArrayList<Card> machineCards =  machinePlayer.getCardsPlayer();
        ArrayList<Card> deckCards = deck.getCards();
        ArrayList<Card> auxdeckCards = deck.getAuxCards();
        Card cardOnTable = table.getCurrentCardOnTheTable();

        GameState gameState = new GameState(
                PlayerCards, machineCards,
                deckCards, auxdeckCards,
                cardOnTable
        );

        serializableFileHandler.serialize("game_data.ser", gameState);
        System.out.println("Si se guardo manito, calma! :)))");

    }



    /**
     * Prints the human player's cards on the stack pane.
     */
    public void printCardsHumanPlayer() {
        this.stackPaneCardsPlayer.getChildren().clear();
        List<Card> cards = this.humanPlayer.getCardsPlayer();

        int offset = Math.max(20, 300 / cards.size()); //Cuanto se desplazara cada carta horizontalmente
        int totalWidth = (cards.size() - 1) * offset;
        int startOffset = -totalWidth / 2; // Para centrar horizontalmente

        for (int i = 0; i < cards.size(); i++) {

            Card card = cards.get(i);
            ImageView cardImageView = card.getCard();
            //SE PUEDE SEPARAR, esto es diseño de que el jugador pasa el cursor por encima de la carta y tenga un borde
            //--------------
            // Aplicar borde directamente al ImageView al hacer hover
            cardImageView.setStyle("-fx-effect: dropshadow(gaussian, transparent, 0, 0, 0, 0);");

            cardImageView.setOnMouseEntered(e -> {
                cardImageView.setScaleX(1.05);
                cardImageView.setScaleY(1.05);
                cardImageView.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);");
            });

            cardImageView.setOnMouseExited(e -> {
                cardImageView.setScaleX(1.0);
                cardImageView.setScaleY(1.0);
                cardImageView.setStyle("-fx-effect: dropshadow(gaussian, transparent, 0, 0, 0, 0);");
            });
            /*
            * Aqui es donde Player Juega una carta
            * */
            cardImageView.setOnMouseClicked((MouseEvent event) -> {
                labelAlertMachine.setText("");
                if(table.isValidPlay(card) ) {

                    // Usamos la clase Animations
                    Animations.playCardAnimation(card, cardImageView, tableImageView, () -> {
                        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                        //si llega aqui, es que se PUSO una carta entonces -> guardammos en AUX
                        deck.PushToAuxDeck(card); //ya la puso, ya no la tiene ni el humano, ni el deck, pasemoloslo al aux
                        System.out.println("*/*/*/*/*/*/*/*/CANTIDAD DE CARTAS EN EL MAZO AUXILIAR: " + deck.getAuxDeckSize());
                        //prueba para pillar que si guarde el serializable OJO VIVO
                        saveGame();
                        //mini prueba para ver que si se guarde la carta actual
                        //System.out.println("CARTA ACTUAL EN LA MESA: " + table.getCurrentCardOnTheTable());
                        //Si al jugador le queda EXACTAMENTE una carta, empieza la vigilancia del uno
                        if (humanPlayer.getCardsPlayer().size() == 1 && !unoCheckStarted) {
                            unoCheckStarted = true; //Evita que se lance mas de una vez
                            checkUNO("PLAYER"); //Simula que la maquina espera a ver si el jugador dice uno
                        }

                        //Condicional para que si el jugador usa el reserve o el skip, no se le deshabilite el deck
                        //y este pueda seguir tomando cartas
                        if (card.getValue().equals("SKIP") || card.getValue().equals("RESERVE")) {
                            imageViewDeck.setOpacity(1);
                            buttonDeck.setDisable(false);
                        } else {
                            imageViewDeck.setOpacity(0.5);
                            buttonDeck.setDisable(true);
                        }

                    /*
                    hacemos la verificacion de si la carta jugada es un comodin, lo hacemos antes de usar el metodo
                    setHasPlayerPlayed para que la maquina no pueda jugar aun, mientras hacemos las validaciones y demas
                     */
                        if (card.isSpecial()) { //si ES especial
                            Platform.runLater(() -> handleSpecialCard(card, machinePlayer)); //dependiendo del caso, aplique efecto, Platform para que
                            //Ese codigo se ejecute despues de que JavaFX haya terminado de procesar eventos actuales y no crashee con la animacion
                        } else { //si no es especial... (normal )
                            threadPlayMachine.setHasPlayerPlayed(true); //dele turno a la machin
                        }
                        printCardsHumanPlayer();
                        //esto iria con un condicional y pondriamos una alerta o algo asi
                        gameUno.isGameOver();

                    });
                }
            });
            //Contenedor para superposición, sin bloquear clicks
            StackPane container = new StackPane(cardImageView);
            container.setPickOnBounds(false); //<-- evita bloquear otras cartas
            container.setTranslateX(startOffset + i * offset);
            this.stackPaneCardsPlayer.getChildren().add(container);
        }
    }


    /**
     * Este metodo verifica quién dice "UNO" primero: el jugador o la máquina.
     * Dependiendo del parámetro "who", maneja los dos casos:
     * - Si es "PLAYER", se espera que el jugador diga UNO antes que la máquina.
     * - Si es "MACHINE", se espera que el jugador acuse a la máquina antes de que ella lo diga.
     *
     *  @param who Cadena que representa quién tiene una carta. Puede ser "PLAYER" o "MACHINE".
     *  */
    private void checkUNO(String who) {

        //Primer caso: Es el jugador quien tiene solo una carta
        if (who.equals("PLAYER")) {
            unoCheckStarted = true;//Se activa la bandera que indica que ya estamos revisando si el jugador dice UNO
            playerSaidUNO = false;  // ← Reiniciar bandera para evitar heredar valor anterior
            System.out.println("El jugador tiene solo una carta, esperando quién dice UNO primero...");

            new Thread(() -> { //Creamos un nuevo hilo para no bloquear la interfaz gráfica
                try {
                    //Esperamos entre 1 y 3 segundos (simula el tiempo que tarda la máquina en decir UNO)
                    int delay = 1000 + new Random().nextInt(2000);
                    Thread.sleep(delay);

                    //Si el jugador NO dijo UNO en ese tiempo, es penalizado
                    if (!playerSaidUNO) {
                        try {
                            //Excepcion marcada
                            throw new PenaltyException("El jugador no dijo UNO a tiempo.", "PLAYER");
                        } catch (PenaltyException e) {

                            // Volvemos al hilo de la interfaz para modificar componentes visuales
                            Platform.runLater(() -> {
                                Card penaltyCard = deck.takeCard();
                                humanPlayer.getCardsPlayer().add(penaltyCard); // Lógica del juego

                                // Hacemos la animación desde el mazo hasta la mano del jugador
                                Animations.animateCardFromDeck(
                                        Card.getBackImage(),            // Imagen boca abajo
                                        imageViewDeck,                  // Nodo de origen (mazo)
                                        stackPaneCardsPlayer,          // Nodo destino (mano del jugador)
                                        false,                          // false porque no es la máquina
                                        () -> printCardsHumanPlayer()  // Acción que actualiza la mano
                                );

                                // Mostramos la alerta
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("UNO");
                                alert.setHeaderText("¡La máquina dijo UNO primero!");
                                alert.setContentText("Has sido penalizado con una carta.");
                                alert.showAndWait();
                            });
                        }
                    } else {
                        // Si el jugador dijo UNO a tiempo, no pasa nada
                        System.out.println("El jugador dijo UNO a tiempo.");
                    }

                    // Reiniciamos las banderas para que pueda volver a usarse el sistema
                    playerSaidUNO = false;
                    unoCheckStarted = false;

                } catch (InterruptedException e) {
                    // Capturamos cualquier interrupción del hilo
                    e.printStackTrace();
                }
            }).start();

            //Segundo caso: Es la máquina quien tiene una sola carta
        } else if (who.equals("MACHINE")) {
            unoCheckMachineStarted = true; //Activamos la bandera de que estamos esperando si la máquina dice UNO
            machineSaidUNO = false; // Reiniciar bandera aquí para evitar que herede el valor anterior
            System.out.println("La máquina tiene solo una carta. Esperando si el jugador le canta...");

            new Thread(() -> { //También usamos un hilo para no bloquear la interfaz
                try {
                    //Tiempo de reacción de la máquina (simula que ella va a decir UNO)
                    int delay = 1000 + new Random().nextInt(2000);
                    Thread.sleep(delay);

                    // Si la máquina aún no ha sido acusada, se autodefiende diciendo UNO
                    if (!machineSaidUNO) {
                        machineSaidUNO = true;
                        System.out.println("La máquina dijo UNO a tiempo.");
                    } else {
                        try {
                            throw new PenaltyException("La máquina no dijo UNO a tiempo.", "MACHINE");
                        } catch (PenaltyException e) {
                            Platform.runLater(() -> {
                                if (e.getPenalizedEntity().equals("MACHINE")) {
                                    machinePlayer.addCard(deck.takeCard());

                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Penalización a la Máquina");
                                    alert.setHeaderText("¡Le cantaste UNO primero a la máquina!");
                                    alert.setContentText("La máquina fue penalizada con una carta.");
                                    alert.showAndWait();
                                }
                            });
                        }
                    }

                    unoCheckMachineStarted = false;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }



    public void printCardsMachinePlayer() {
        this.stackPaneCardsMachine.getChildren().clear();
        this.machineCardViews.clear(); // <--- LIMPIAMOS EL MAPA

        List<Card> cards = this.machinePlayer.getCardsPlayer();
        int numCards = cards.size();
        if (numCards == 0) return;

        int offset = Math.max(20, 300 / numCards); // Mismo cálculo que el jugador
        int totalWidth = (numCards - 1) * offset;
        int startOffset = -totalWidth / 2; // Centrado horizontal


        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            ImageView cardImageView = card.createCardImageViewBack(); //Reverso

            // Posición explícita en el Pane
            cardImageView.setLayoutX(150 + i * offset); // puedes ajustar 150 según el centro
            cardImageView.setLayoutY(0);

            stackPaneCardsMachine.getChildren().add(cardImageView);
            machineCardViews.put(card, cardImageView); // Guardamos la referencia
        }


        // Si la máquina ya no tiene solo una carta, reiniciamos las banderas UNO
        if (machinePlayer.getCardsPlayer().size() != 1) {
            unoCheckMachineStarted = false;
            machineSaidUNO = false;
        }

        //Si a la maquina le queda EXACTAMENTE una carta, empieza la vigilancia del uno
        if (machinePlayer.getCardsPlayer().size() == 1 && !unoCheckMachineStarted) {
            unoCheckMachineStarted = true; //Evita que se lance mas de una vez
            checkUNO("MACHINE"); //Simula que el jugador espera a ver si la maquina dice uno
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
        //Desactivamos de inmediato para evitar doble click
        buttonDeck.setDisable(true);
        labelAlertMachine.setText(""); //limpio el label
        /*OJO VIVO, tenemos que colocar esta condicion como que si el mazo llega a tener 5 o menos cartas
        para hacer el refill, ya que si se deja en cuando quede vacio, si la ultima carta en ser lanzada
        llega a ser un +2 o +4, te deja viendo un chispero :(
        el MINIMO de cartas que debe haber para que sea jugable es de 4. */
        if(deck.getDeckSize()<=4) { //si hay 4 o menos cartas...
            System.out.println("Mazo vacio ----> RELLENANDO"); //avisamos que se rellena
            deck.RefillCards(); //llamamos metodo para rellenar!, el resto de codigo sigue igual...xd

            // Animación con carta boca abajo
            Image cardBackImage = new Image("/org/example/eiscuno/cards-uno/card_uno.png");
            Animations.animateCardFromDeck(
                    cardBackImage,
                    imageViewDeck,
                    stackPaneCardsPlayer,
                    false, // no es máquina
                    () -> {
                        humanPlayer.addCard(deck.takeCard()); //se lo sumamos al humano
                        imageViewDeck.setOpacity(0.5);
                        buttonDeck.setDisable(true);
                        threadPlayMachine.setHasPlayerPlayed(true);
                        printCardsHumanPlayer();
                    }
            );
        } else {
            // Animación con carta boca abajo
            Image cardBackImage = new Image("/org/example/eiscuno/cards-uno/card_uno.png");
            Animations.animateCardFromDeck(
                    cardBackImage,
                    imageViewDeck,
                    stackPaneCardsPlayer,
                    false, // no es máquina
                    () -> {
                        humanPlayer.addCard(deck.takeCard()); //se lo sumamos al humano
                        imageViewDeck.setOpacity(0.5);
                        threadPlayMachine.setHasPlayerPlayed(true);
                        printCardsHumanPlayer();
                    }
            );
        }
    }

    /**
     * Handles the action of saying "Uno".
     *
     * @param event the action event
     */
    //Metodo que cubre al momento de presionar "uno" cuando la maquina tiene una carta y cuando el jugador tiene una carta
    //Siendo el primer caso el jugador teniendo una sola carta y segundo caso la maquina teniendo una sola carta, y el caso invalido donde no se puede decir uno
    @FXML
    void onHandleUno(ActionEvent event) {
        //Primer caso: El jugador tiene una sola carta y está en verificación de UNO
        if (humanPlayer.getCardsPlayer().size() == 1 && unoCheckStarted) {
            playerSaidUNO = true; //Se registra que el jugador sí dijo UNO
            System.out.println("El jugador presionó el botón UNO a tiempo.");

            //Segundo caso: El jugador intenta acusar a la máquina cuando esta tiene una sola carta
        } else if (machinePlayer.getCardsPlayer().size() == 1 && unoCheckMachineStarted && !machineSaidUNO) {
            System.out.println("El jugador acusó a la máquina por no decir UNO a tiempo.");

            //Penalizamos a la máquina
            machinePlayer.addCard(deck.takeCard());

            //Actualiza la vista de la maquina de inmediato
            printCardsMachinePlayer();

            // Animación: carta del mazo a la máquina (boca abajo)
            Animations.animateCardFromDeck(
                    Card.getBackImage(),               // Imagen del reverso
                    imageViewDeck,                     // Mazo
                    stackPaneCardsMachine,             // Mano de la máquina
                    true,                              // Es máquina
                    () -> printCardsMachinePlayer()    // Actualizar visual
            );

            //Reiniciamos banderas de vigilancia de UNO
            unoCheckMachineStarted = false;
            machineSaidUNO = false;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("UNO");
            alert.setHeaderText("¡Acusación exitosa!");
            alert.setContentText("La máquina no dijo UNO a tiempo y ha sido penalizada.");
            alert.showAndWait();

            //Caso inválido: el jugador no puede decir UNO
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("UNO");
            alert.setHeaderText("No puedes decir UNO ahora");
            alert.setContentText("Solo puedes decir UNO cuando te queda una sola carta, o acusar a la máquina si ella no ha dicho UNO.");
            alert.showAndWait();
        }
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
    //Nota d Pipe: cambio lo del two wild y +4 para agregar la animacion
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
                        labelAlertMachine.setText("La maquina vuelve a tirar");
                        threadPlayMachine.setHasPlayerPlayed(true); //sigue jugando la machin, skipea player
                        System.out.println("El turno : " + threadPlayMachine.getHasPlayerPlay());
                    }
                    break;
                case "RESERVE":
                    System.out.println("RESERVE USED!");
                    if (targetPlayer == machinePlayer) {
                        threadPlayMachine.setHasPlayerPlayed(false); //sigue jugando el jugador, se skipeo machin
                    }else{
                        labelAlertMachine.setText("La maquina vuelve a tirar");
                        threadPlayMachine.setHasPlayerPlayed(true); //sigue jugando la machin, skipea player
                        System.out.println("El turno : " + threadPlayMachine.getHasPlayerPlay());
                    }
                    break;
                case "WILD":
                    /*
                    implementacion de crear un menu interactivo para escoger el color que se escoge con la carta WILD.
                    */
                    System.out.println("WILD USED!");
                    if (targetPlayer == machinePlayer) {
                        printCardsHumanPlayer();

                        //logica para cambiar el color del juego
                        Optional<String> result = dialog.showAndWait();
                        result.ifPresent(color -> {
                            System.out.println("Color seleccionado: " + color);
                            //el jugador escoge un color, entonces la carda se le setea ese color para que ese sea el color valido para continuar jugando
                            card.setColor(translateColor(color));
                        });
                        threadPlayMachine.setHasPlayerPlayed(true); //se le da el turno a la maquina
                    }else{
                        Random random = new Random();
                        int index = random.nextInt(options.size());
                        String color = options.get(index);
                        card.setColor(translateColor(color));
                        System.out.println("Color escogido: " + color);
                        labelAlertMachine.setText("La maquina escogió el color: " + color);
                        imageViewDeck.setOpacity(1);
                        buttonDeck.setDisable(false);
                        threadPlayMachine.setHasPlayerPlayed(false); //se le da el turno al jugador
                        System.out.println("El turno : " + threadPlayMachine.getHasPlayerPlay());
                    }
                    break;
                case "TWO_WILD":
                    System.out.println("TWO_WILD USED! +2");
                    if (targetPlayer == machinePlayer) { //si el jugador tiro el +2
                        Animations.animateEatCards(machinePlayer, 2, true, gameUno, this); // animación y logica
                        labelAlertMachine.setText("La maquina comió 2 cartas");
                        threadPlayMachine.setHasPlayerPlayed(true); //el turno pasa a ser de ella
                    }else{ //si lo tiro la machin
                        Animations.animateEatCards(humanPlayer, 2, false, gameUno, this);
                        imageViewDeck.setOpacity(1);
                        buttonDeck.setDisable(false);
                        threadPlayMachine.setHasPlayerPlayed(false); //el turno ahora es del player
                        System.out.println("El turno : " + threadPlayMachine.getHasPlayerPlay());
                    }
                    break;
                case "FOUR_WILD":
                    System.out.println("FOUR_WILD USED! +4");
                    if (targetPlayer == machinePlayer) { //si el jugador tiro el +4
                        Animations.animateEatCards(machinePlayer, 4, true, gameUno, this);
                        labelAlertMachine.setText("La maquina comió 4 cartas");

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
                        Animations.animateEatCards(humanPlayer, 4, false, gameUno, this);
                        Random random = new Random();
                        int index = random.nextInt(options.size());
                        String color = options.get(index);
                        card.setColor(translateColor(color));
                        System.out.println("Color escogido: " + color);
                        labelAlertMachine.setText("La maquina escogió el color: " + color);
                        imageViewDeck.setOpacity(1);
                        buttonDeck.setDisable(false);
                        threadPlayMachine.setHasPlayerPlayed(false);//el turno ahora es del player
                        System.out.println("El turno : " + threadPlayMachine.getHasPlayerPlay());
                    }
                    break;
                default:
                    System.out.println("Error, caso NO manejado!");

            }
    }

    public void deactivateEmptyDeck() {
        if(deck.isEmpty()) {
            imageViewDeck.setOpacity(0.5);
            buttonDeck.setDisable(true);
        }
    }

    @FXML
    void onHandleExit(ActionEvent event) throws IOException {
        GameUnoStage.deleteInstance();
        //modifico esto para que cuando le des a salir no se salga del todou de la app,
        //mejor que nos mande primero al menu. asi es mas facil hacer pruebas tbn
        StartUnoView.getInstance();
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

    public Label getLabelAlertMachine() {
        return labelAlertMachine;
    }
}
