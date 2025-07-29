package org.example.eiscuno.controller;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;

public class Animations {

    //Metodo para hacer que una carta se arrastre desde su posicion al centro de la mesa
    public static void playCardAnimation(
            Card card,
            ImageView cardImageView,
            ImageView tableImageView,
            Runnable onAnimationFinished
    ) {
        // 1. Crear duplicado de la carta
        ImageView animatedCard = new ImageView(card.getImage());
        animatedCard.setFitWidth(cardImageView.getFitWidth());
        animatedCard.setFitHeight(cardImageView.getFitHeight());

        // 2. Obtener posición inicial
        Bounds startBounds = cardImageView.localToScene(cardImageView.getBoundsInLocal());
        animatedCard.setTranslateX(startBounds.getMinX());
        animatedCard.setTranslateY(startBounds.getMinY());

        // 3. Agregar a la escena
        Scene scene = cardImageView.getScene();
        Pane root = (Pane) scene.getRoot();
        root.getChildren().add(animatedCard);

        // 4. Calcular destino (centro de la mesa)
        Bounds endBounds = tableImageView.localToScene(tableImageView.getBoundsInLocal());
        double endX = endBounds.getMinX() + tableImageView.getFitWidth() / 2 - cardImageView.getFitWidth() / 2;
        double endY = endBounds.getMinY() + tableImageView.getFitHeight() / 2 - cardImageView.getFitHeight() / 2;

        // 5. Crear y configurar transición
        TranslateTransition transition = new TranslateTransition(Duration.millis(500), animatedCard);
        transition.setToX(endX);
        transition.setToY(endY);
        transition.setInterpolator(Interpolator.EASE_OUT);

        // 6. Lógica al finalizar la animación
        transition.setOnFinished(event -> {
            tableImageView.setImage(card.getImage());
            root.getChildren().remove(animatedCard);

            // Ejecutar lógica adicional (guardado, efectos, turnos, etc)
            if (onAnimationFinished != null) {
                Platform.runLater(onAnimationFinished);
            }
        });

        // 7. Ejecutar animación
        transition.play();
    }

    //Misma animacion que la de arriba pero para la maquina
    public static void playCardFromMachine(Card card, ImageView originalCardView, ImageView tableImageView, Runnable onAnimationFinished) {
        // 1. Crear duplicado de la carta
        ImageView animatedCard = new ImageView(card.getImage());
        animatedCard.setFitWidth(originalCardView.getFitWidth());
        animatedCard.setFitHeight(originalCardView.getFitHeight());

        // 2. Obtener posición inicial
        Bounds startBounds = originalCardView.localToScene(originalCardView.getBoundsInLocal());
        animatedCard.setTranslateX(startBounds.getMinX());
        animatedCard.setTranslateY(startBounds.getMinY());

        // 3. Agregar a la escena
        Scene scene = originalCardView.getScene();
        Pane root = (Pane) scene.getRoot();
        root.getChildren().add(animatedCard);

        // 4. Calcular destino (centro de la mesa)
        Bounds endBounds = tableImageView.localToScene(tableImageView.getBoundsInLocal());
        double endX = endBounds.getMinX() + tableImageView.getFitWidth() / 2 - animatedCard.getFitWidth() / 2;
        double endY = endBounds.getMinY() + tableImageView.getFitHeight() / 2 - animatedCard.getFitHeight() / 2;

        // 5. Crear transición
        TranslateTransition transition = new TranslateTransition(Duration.millis(500), animatedCard);
        transition.setToX(endX);
        transition.setToY(endY);
        transition.setInterpolator(Interpolator.EASE_OUT);

        // 6. Al terminar, actualizar imagen en la mesa y limpiar
        transition.setOnFinished(event -> {
            tableImageView.setImage(card.getImage());
            root.getChildren().remove(animatedCard);

            if (onAnimationFinished != null) {
                Platform.runLater(onAnimationFinished);
            }
        });

        // 7. Ejecutar
        transition.play();
    }

    //Metodo generico de animacion de el mazo hasta el jugador o la maquina
    public static void animateCardFromDeck(
            Image cardImage,
            ImageView deckImageView,
            Pane targetPane,
            boolean isMachine,
            Runnable onFinished
    ) {
        ImageView animatedCard = new ImageView(cardImage);
        animatedCard.setFitWidth(80);
        animatedCard.setFitHeight(120);

        Scene scene = deckImageView.getScene();
        if (scene == null) {
            System.out.println("⚠ Escena nula. No se puede animar carta desde el mazo.");
            if (onFinished != null) onFinished.run();
            return;
        }

        Pane root = (Pane) scene.getRoot();
        Bounds startBounds = deckImageView.localToScene(deckImageView.getBoundsInLocal());
        animatedCard.setTranslateX(startBounds.getMinX());
        animatedCard.setTranslateY(startBounds.getMinY());

        // Posición final: mano del jugador o máquina
        Bounds endBounds = targetPane.localToScene(targetPane.getBoundsInLocal());

        double offsetX = Math.random() * 50 - 25;
        double offsetY = isMachine ? -30 : 30;

        double endX = endBounds.getMinX() + targetPane.getWidth() / 2 + offsetX;
        double endY = endBounds.getMinY() + offsetY;

        root.getChildren().add(animatedCard);

        TranslateTransition transition = new TranslateTransition(Duration.millis(400), animatedCard);
        transition.setToX(endX);
        transition.setToY(endY);
        transition.setInterpolator(Interpolator.EASE_OUT);

        transition.setOnFinished(e -> {
            root.getChildren().remove(animatedCard);
            if (onFinished != null) Platform.runLater(onFinished);
        });

        transition.play();
    }


    public static void animateEatCards(Player player, int count, boolean isMachine, GameUno gameUno, GameUnoController controller) {
        AnchorPane root = (AnchorPane) controller.imageViewDeck.getScene().getRoot();
        Timeline timeline = new Timeline();

        for (int i = 0; i < count; i++) {
            int index = i;

            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5 * index), e -> {
                ImageView tempCard = new ImageView(new Image("/org/example/eiscuno/cards-uno/card_uno.png"));
                tempCard.setFitWidth(80);
                tempCard.setFitHeight(120);

                // Coordenadas de inicio: baraja (imageViewDeck)
                Bounds deckBoundsScene = controller.imageViewDeck.localToScene(controller.imageViewDeck.getBoundsInLocal());
                Point2D deckInRoot = root.sceneToLocal(deckBoundsScene.getMinX(), deckBoundsScene.getMinY());
                double startX = deckInRoot.getX();
                double startY = deckInRoot.getY();
                tempCard.setLayoutX(startX);
                tempCard.setLayoutY(startY);

                // Solo se añade al root después de posicionarla
                root.getChildren().add(tempCard);

                // Coordenadas de destino: mano de jugador o máquina
                Bounds targetBoundsScene = isMachine
                        ? controller.stackPaneCardsMachine.localToScene(controller.stackPaneCardsMachine.getBoundsInLocal())
                        : controller.stackPaneCardsPlayer.localToScene(controller.stackPaneCardsPlayer.getBoundsInLocal());
                Point2D targetInRoot = root.sceneToLocal(
                        targetBoundsScene.getMinX() + targetBoundsScene.getWidth() / 2,
                        targetBoundsScene.getMinY() + targetBoundsScene.getHeight() / 2
                );
                double endX = targetInRoot.getX() - 40; // centrado (mitad del ancho de la carta)
                double endY = targetInRoot.getY() - 60; // centrado (mitad del alto de la carta)

                // Animación de traslado
                TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4), tempCard);
                transition.setToX(endX - startX);
                transition.setToY(endY - startY);
                transition.setOnFinished(ev -> {
                    root.getChildren().remove(tempCard);  // eliminar carta animada
                    gameUno.eatCard(player, 1);           // lógica del juego
                    if (isMachine) {
                        controller.printCardsMachinePlayer();
                    } else {
                        controller.printCardsHumanPlayer();
                    }
                });

                transition.play();
            });

            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();
    }



    // Otros métodos de animación...
    }




