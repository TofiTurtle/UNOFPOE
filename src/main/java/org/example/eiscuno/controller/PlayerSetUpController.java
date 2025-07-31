package org.example.eiscuno.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.eiscuno.model.saveGame.GameState;
import org.example.eiscuno.model.saveGame.PlainTextFileHandler;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.PlayerSetUpStage;
import org.example.eiscuno.view.StartUnoView;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for the player setup screen in UNO game.
 * Handles player avatar selection, name input, and game initialization.
 */
public class PlayerSetUpController {
    @FXML
    private ImageView imageView;
    @FXML
    private TextField textField;
    @FXML
    private Label emptyNameLabel;
    private List<Image> images;
    private int currentIndex = 2;

    // File handler for player data persistence
    private PlainTextFileHandler plainTextFileHandler;

    // Array containing paths to available player images
    private String PathListImages[] = { "/org/example/eiscuno/images/player1.jpg",
            "/org/example/eiscuno/images/player2.jpg",
            "/org/example/eiscuno/images/player3.jpg",
            "/org/example/eiscuno/images/player4.png"};

    /**
     * Initializes the controller class.
     * Sets up the initial player avatar and text field listener.
     */
    @FXML
    public void initialize() {
        // Listener for text field changes to hide empty name warning
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                emptyNameLabel.setVisible(false);
            }
        });

        // Load all available player images
        images = List.of(
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/images/player1.jpg")),
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/images/player2.jpg")),
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/images/player3.jpg")),
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/images/player4.png"))
        );

        // Set default image
        imageView.setImage(images.get(currentIndex));
        plainTextFileHandler = new PlainTextFileHandler();
    }

    /**
     * Cycles to the next available player avatar image.
     * Wraps around to the first image when reaching the end.
     */
    @FXML
    private void nextImage() {
        if (currentIndex < images.size() - 1) {
            currentIndex++;
            imageView.setImage(images.get(currentIndex));
        } else {
            currentIndex = 0;
            imageView.setImage(images.get(currentIndex));
        }
    }

    /**
     * Cycles to the previous available player avatar image.
     * Wraps around to the last image when reaching the beginning.
     */
    @FXML
    private void previousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            imageView.setImage(images.get(currentIndex));
        } else {
            currentIndex = images.size() - 1;
            imageView.setImage(images.get(currentIndex));
        }
    }

    /**
     * Handles the back button action.
     * Returns to the game's start screen.
     *
     * @param event The action event triggered by the button
     * @throws IOException if there's an error loading the start view
     */
    @FXML
    void goBackToStart(ActionEvent event) throws IOException {
        StartUnoView.getInstance();
        PlayerSetUpStage.deleteInstance();
    }

    /**
     * Handles the game start button action.
     * Saves player data and launches the main game screen.
     *
     * @param event The action event triggered by the button
     * @throws IOException if there's an error saving player data or loading the game
     */
    @FXML
    void startGame(ActionEvent event) throws IOException {
        String name = textField.getText().trim();
        String currentImage = PathListImages[currentIndex];

        // Save player data to file (name and selected image path)
        String content = name + "," + currentImage;
        plainTextFileHandler.writeToFile("player_data.csv", content);

        // Initialize new game state (null indicates new game)
        GameState gameState = null;

        // Launch main game screen with player data
        GameUnoStage.getInstance(name, currentImage, gameState);
        PlayerSetUpStage.deleteInstance();
    }
}