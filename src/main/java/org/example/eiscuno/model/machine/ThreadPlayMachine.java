package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.controller.Animations;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javafx.scene.layout.StackPane;


public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private Deck deck;
    private volatile boolean hasPlayerPlayed;
    private GameUnoController gameUnoController;
    private Map<Card, ImageView> machineCardViews;
    private Pane stackPaneCardsMachine; // ⬅️ Añadido aquí


    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, Deck deck, GameUnoController gameUnoController, Map<Card, ImageView> machineCardViews, Pane stackPaneCardsMachine) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
        this.deck = deck;
        this.gameUnoController = gameUnoController;
        this.machineCardViews = machineCardViews;
        this.stackPaneCardsMachine = stackPaneCardsMachine; // ⬅️ Asignado aquí

    }

    public void run() {
        while (true) {
            if (hasPlayerPlayed) {
                // desactivar UI del jugador
                Platform.runLater(() -> gameUnoController.stackPaneCardsPlayer.setDisable(true));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Card cardPlayed = putCardOnTheTable();

                Platform.runLater(() -> gameUnoController.printCardsMachinePlayer());

                if (cardPlayed == null) {
                    handleTakeCard();
                    Platform.runLater(() -> {
                        gameUnoController.getLabelAlertMachine().setText("La maquina arrastró una carta");
                    });
                    hasPlayerPlayed = false;
                }
                else if (cardPlayed.isSpecial()) {
                    // esperamos a que handleSpecialCard termine de ejecutarse en el FX‐thread
                    CountDownLatch latch = new CountDownLatch(1);
                    Platform.runLater(() -> {
                        gameUnoController.handleSpecialCard(cardPlayed, gameUnoController.getHumanPlayer());
                        latch.countDown();
                        //La maquina aqui PUSO una carta ESPECIAL, como PUSO-> la guardamos en el auxiliar
                        deck.PushToAuxDeck(cardPlayed);
                        System.out.println("*/*/*/*/*/*/*/*/CANTIDAD DE CARTAS EN EL MAZO AUXILIAR: "+ deck.getAuxDeckSize());
                    });
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                else {
                    // carta normal → pasa el turno a humano
                    gameUnoController.imageViewDeck.setOpacity(1);
                    gameUnoController.buttonDeck.setDisable(false);
                    hasPlayerPlayed = false;
                    //Aqui la maquina tiro una carta NORMAL, como PUSO-> Guardamos en auxiliar
                    deck.PushToAuxDeck(cardPlayed);
                    System.out.println("*/*/*/*/*/*/*/*/CANTIDAD DE CARTAS EN EL MAZO AUXILIAR: "+ deck.getAuxDeckSize());
                }

                // reactivar UI del jugador
                Platform.runLater(() -> gameUnoController.stackPaneCardsPlayer.setDisable(false));
            }
        }
    }

    // este metodo devuelve la carta que se jugo o null en el caso de que no tuviera carta valida para jugar
    private Card putCardOnTheTable(){

        //se crea una copia de el mazo actual de la maquina para iterar sobre esta
        ArrayList<Card> machineDeck = new ArrayList<>(machinePlayer.getCardsPlayer());
        //Para verificar
        System.out.println("----------------------------------------------\n" +
                           "       Mazo Maquina Antes de Lanzar: ");
        for(int i = 0; i < machineDeck.size(); i++) {
            System.out.print( machineDeck.get(i).getColor() + ": " + machineDeck.get(i).getValue() + "\n");
        }
        System.out.println("----------------------------------------------\n");

        int index = (int) (Math.random() * machineDeck.size());
        Card selectedCard;


        //iteramos sobre el mazo de la maquina uno por uno comprobando  que se pueda lanzar una carta
        for(int i = 0; i < machineDeck.size(); i++) {
            selectedCard = machineDeck.get(i);
            if(table.isValidPlay(selectedCard)) {
                Card finalSelectedCard = selectedCard; // ✅ ahora es final
                final ImageView finalTableImage = tableImageView;

                //si la carta fue valida entonces la borro de el mazo original de la maquina y la seteo en la mesa
                machinePlayer.getCardsPlayer().remove(selectedCard);
                tableImageView.setImage(selectedCard.getImage());

                Platform.runLater(() -> {
                    ImageView iv = machineCardViews.get(finalSelectedCard);

                    if (iv != null) {
                        // 💡 Asegurarse que esté en el Pane antes de animar
                        if (!stackPaneCardsMachine.getChildren().contains(iv)) {
                            stackPaneCardsMachine.getChildren().add(iv);
                        }

                        Animations.playCardFromMachine(finalSelectedCard, iv, finalTableImage, () -> {
                            Platform.runLater(() -> stackPaneCardsMachine.getChildren().remove(iv));
                        });

                    } else {
                        System.out.println("⚠ No se encontró la carta en machineCardViews");
                    }
                });

                System.out.println("----------------------------------------------\n" +
                        "       Mazo Maquina DESPUES de Lanzar: ");
                for(int j = 0; j < machinePlayer.getCardsPlayer().size(); j++) {
                    System.out.print( machinePlayer.getCardsPlayer().get(j).getColor() + ": " + machinePlayer.getCardsPlayer().get(j).getValue() + "\n");
                }
                System.out.println("----------------------------------------------\n");

                //actualizo el label con la informacion de que se jugo una carta
                Platform.runLater(() -> {
                    gameUnoController.getLabelAlertMachine().setText("La maquina jugó una carta");
                });

                //retorno la carta jugada
                return selectedCard;
            }
        }

        // Si en el ciclo anterior no se logro tirar ninguna carta, entonces devuelve null
        return null;
    }

    /*
    Metodo que maneja el que la maquina tome una carta y despues ceda el turno al jugador
     */
    private void handleTakeCard() {
        if(deck.isEmpty()) {
            System.out.println("El mazo esta vacio, no se puede arrastrar");
            gameUnoController.deactivateEmptyDeck();
        }
        else {
            Platform.runLater(() -> {
                // Animación con carta boca abajo para la máquina
                Image cardBackImage = new Image("/org/example/eiscuno/cards-uno/card_uno.png");
                Animations.animateCardFromDeck(
                        cardBackImage,
                        gameUnoController.imageViewDeck,
                        gameUnoController.stackPaneCardsMachine,
                        true, // es máquina
                        () -> {
                            // Solo AQUÍ se toma la carta y se agrega, una sola vez
                            machinePlayer.addCard(deck.takeCard());

                            gameUnoController.printCardsMachinePlayer();

                            gameUnoController.imageViewDeck.setOpacity(1);
                            gameUnoController.buttonDeck.setDisable(false);

                            setHasPlayerPlayed(false);
                        }
                );
            });
        }
    }
    public boolean getHasPlayerPlay() {
        return this.hasPlayerPlayed;
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}