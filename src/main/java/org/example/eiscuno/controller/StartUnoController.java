package org.example.eiscuno.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.example.eiscuno.model.saveGame.GameState;
import org.example.eiscuno.model.saveGame.PlainTextFileHandler;
import org.example.eiscuno.model.saveGame.SerializableFileHandler;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.PlayerSetUpStage;
import org.example.eiscuno.view.StartUnoView;
import java.io.IOException;

/**
 * Controller class for the game's start screen.
 * Handles navigation to player setup, game loading, and application exit.
 */
public class StartUnoController {

    /**
     * Handles the exit game button action.
     * Closes the application window.
     *
     * @param event The action event triggered by the button
     */
    @FXML
    void exitGame(ActionEvent event) {
        StartUnoView.deleteInstance();
    }

    // Handler for plain text file operations
    private PlainTextFileHandler plainTextFileHandler = new PlainTextFileHandler();

    // Handler for serialized game data operations
    private SerializableFileHandler serializableFileHandler = new SerializableFileHandler();

    /**
     * Handles the load game button action.
     * Loads saved player data and game state to continue previous game.
     *
     * @param event The action event triggered by the button
     * @throws IOException if there's an error reading saved data
     */
    @FXML
    void loadGame(ActionEvent event) throws IOException {
        // Read player data from file
        String[] data = plainTextFileHandler.readFromFile("player_data.csv");
        String playerName = data[0]; // Player name
        String characterImagePath = data[1]; // Character image path

        // Load serialized game state
        GameState gameState = (GameState) serializableFileHandler.deserialize("game_data.ser");

        // Launch game with loaded data
        GameUnoStage.getInstance(playerName, characterImagePath, gameState);
        StartUnoView.deleteInstance();
    }

    /**
     * Handles the new game button action.
     * Navigates to player setup screen to start new game.
     *
     * @param event The action event triggered by the button
     * @throws IOException if there's an error loading the player setup screen
     */
    @FXML
    void goToPlayerSetUp(ActionEvent event) throws IOException {
        // Navigate to player setup screen
        PlayerSetUpStage.getInstance();
        // Close current start screen
        StartUnoView.deleteInstance();
    }
}