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

/**
 * Utility class for handling all card animations in the UNO game.
 * Provides methods for playing cards, dealing cards from deck, and animating card eating.
 */
public class Animations {

    /**
     * Plays the animation of a card moving from its current position to the center of the table.
     *
     * @param card The card object being animated
     * @param cardImageView The ImageView of the card to animate
     * @param tableImageView The ImageView representing the table center
     * @param onAnimationFinished Runnable to execute after animation completes
     */
    public static void playCardAnimation(
            Card card,
            ImageView cardImageView,
            ImageView tableImageView,
            Runnable onAnimationFinished
    ) {
        // 1. Create card duplicate for animation
        ImageView animatedCard = new ImageView(card.getImage());
        animatedCard.setFitWidth(cardImageView.getFitWidth());
        animatedCard.setFitHeight(cardImageView.getFitHeight());

        // 2. Get initial position
        Bounds startBounds = cardImageView.localToScene(cardImageView.getBoundsInLocal());
        animatedCard.setTranslateX(startBounds.getMinX());
        animatedCard.setTranslateY(startBounds.getMinY());

        // 3. Add to scene
        Scene scene = cardImageView.getScene();
        Pane root = (Pane) scene.getRoot();
        root.getChildren().add(animatedCard);

        // 4. Calculate destination (table center)
        Bounds endBounds = tableImageView.localToScene(tableImageView.getBoundsInLocal());
        double endX = endBounds.getMinX() + tableImageView.getFitWidth() / 2 - cardImageView.getFitWidth() / 2;
        double endY = endBounds.getMinY() + tableImageView.getFitHeight() / 2 - cardImageView.getFitHeight() / 2;

        // 5. Create and configure transition
        TranslateTransition transition = new TranslateTransition(Duration.millis(500), animatedCard);
        transition.setToX(endX);
        transition.setToY(endY);
        transition.setInterpolator(Interpolator.EASE_OUT);

        // 6. Animation completion logic
        transition.setOnFinished(event -> {
            tableImageView.setImage(card.getImage());
            root.getChildren().remove(animatedCard);

            // Execute additional logic (saving, effects, turns, etc)
            if (onAnimationFinished != null) {
                Platform.runLater(onAnimationFinished);
            }
        });

        // 7. Play animation
        transition.play();
    }

    /**
     * Plays the animation of a card being played by the machine (AI player).
     * Similar to playCardAnimation but optimized for machine player.
     *
     * @param card The card being played
     * @param originalCardView The ImageView of the machine's card
     * @param tableImageView The ImageView representing the table center
     * @param onAnimationFinished Callback to execute after animation
     */
    public static void playCardFromMachine(Card card, ImageView originalCardView, ImageView tableImageView, Runnable onAnimationFinished) {
        // 1. Create card duplicate for animation
        ImageView animatedCard = new ImageView(card.getImage());
        animatedCard.setFitWidth(originalCardView.getFitWidth());
        animatedCard.setFitHeight(originalCardView.getFitHeight());

        // 2. Get initial position
        Bounds startBounds = originalCardView.localToScene(originalCardView.getBoundsInLocal());
        animatedCard.setTranslateX(startBounds.getMinX());
        animatedCard.setTranslateY(startBounds.getMinY());

        // 3. Add to scene
        Scene scene = originalCardView.getScene();
        Pane root = (Pane) scene.getRoot();
        root.getChildren().add(animatedCard);

        // 4. Calculate destination (table center)
        Bounds endBounds = tableImageView.localToScene(tableImageView.getBoundsInLocal());
        double endX = endBounds.getMinX() + tableImageView.getFitWidth() / 2 - animatedCard.getFitWidth() / 2;
        double endY = endBounds.getMinY() + tableImageView.getFitHeight() / 2 - animatedCard.getFitHeight() / 2;

        // 5. Create transition
        TranslateTransition transition = new TranslateTransition(Duration.millis(500), animatedCard);
        transition.setToX(endX);
        transition.setToY(endY);
        transition.setInterpolator(Interpolator.EASE_OUT);

        // 6. On completion: update table image and clean up
        transition.setOnFinished(event -> {
            tableImageView.setImage(card.getImage());
            root.getChildren().remove(animatedCard);

            if (onAnimationFinished != null) {
                Platform.runLater(onAnimationFinished);
            }
        });

        // 7. Play animation
        transition.play();
    }

    /**
     * Animates a card moving from the deck to either the player's or machine's hand.
     *
     * @param cardImage The image of the card to animate
     * @param deckImageView The ImageView of the deck
     * @param targetPane The Pane where the card should land (player or machine)
     * @param isMachine Flag indicating if animation is for the machine player
     * @param onFinished Callback to execute after animation completes
     */
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

        // Final position: player's or machine's hand
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

    /**
     * Animates the process of a player (human or machine) eating/drawing cards from the deck.
     *
     * @param player The player who is eating cards
     * @param count Number of cards to animate
     * @param isMachine Flag indicating if animation is for the machine player
     * @param gameUno The game model instance
     * @param controller The game controller instance
     */
    public static void animateEatCards(Player player, int count, boolean isMachine, GameUno gameUno, GameUnoController controller) {
        AnchorPane root = (AnchorPane) controller.imageViewDeck.getScene().getRoot();
        Timeline timeline = new Timeline();

        for (int i = 0; i < count; i++) {
            int index = i;

            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5 * index), e -> {
                ImageView tempCard = new ImageView(new Image("/org/example/eiscuno/cards-uno/card_uno.png"));
                tempCard.setFitWidth(80);
                tempCard.setFitHeight(120);

                // Starting coordinates: deck (imageViewDeck)
                Bounds deckBoundsScene = controller.imageViewDeck.localToScene(controller.imageViewDeck.getBoundsInLocal());
                Point2D deckInRoot = root.sceneToLocal(deckBoundsScene.getMinX(), deckBoundsScene.getMinY());
                double startX = deckInRoot.getX();
                double startY = deckInRoot.getY();
                tempCard.setLayoutX(startX);
                tempCard.setLayoutY(startY);

                // Only add to root after positioning
                root.getChildren().add(tempCard);

                // Destination coordinates: player's or machine's hand
                Bounds targetBoundsScene = isMachine
                        ? controller.stackPaneCardsMachine.localToScene(controller.stackPaneCardsMachine.getBoundsInLocal())
                        : controller.stackPaneCardsPlayer.localToScene(controller.stackPaneCardsPlayer.getBoundsInLocal());
                Point2D targetInRoot = root.sceneToLocal(
                        targetBoundsScene.getMinX() + targetBoundsScene.getWidth() / 2,
                        targetBoundsScene.getMinY() + targetBoundsScene.getHeight() / 2
                );
                double endX = targetInRoot.getX() - 40; // centered (half card width)
                double endY = targetInRoot.getY() - 60; // centered (half card height)

                // Translation animation
                TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4), tempCard);
                transition.setToX(endX - startX);
                transition.setToY(endY - startY);
                transition.setOnFinished(ev -> {
                    root.getChildren().remove(tempCard);  // remove animated card
                    gameUno.eatCard(player, 1);           // game logic
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
}