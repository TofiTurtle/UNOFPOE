package org.example.eiscuno.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Singleton Stage for player setup and character selection.
 * <p>
 * This class implements a singleton pattern to ensure only one instance exists
 * and manages the player configuration screen with full-screen display.
 * </p>
 */
public class PlayerSetUpStage extends Stage {

    /**
     * Constructs the player setup stage.
     * <p>
     * Loads the FXML view, sets up the scene, and configures full-screen properties.
     * The stage cannot be exited accidentally by keyboard shortcuts.
     * </p>
     *
     * @throws IOException if there's an error loading the FXML file
     */
    public PlayerSetUpStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eiscuno/PlayerSetUp.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new IOException("Error al cargar el archvio FXML", e);
        }
        Scene scene = new Scene(root);
        setTitle("Player - Character Selector!");
        setScene(scene);

        // Configure full-screen mode
        setFullScreen(true);
        setFullScreenExitHint("");
        setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);

        // Configure exit control
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE -> PlayerSetUpStage.deleteInstance();
            }
        });

        show();
    }

    /**
     * Holder class for the singleton instance (Bill Pugh pattern).
     */
    private static class PlayerSetUpHolder {
        private static PlayerSetUpStage INSTANCE;
    }

    /**
     * Gets the singleton instance of the player setup stage.
     *
     * @return the singleton instance
     * @throws IOException if there's an error creating the stage
     */
    public static PlayerSetUpStage getInstance() throws IOException {
        return PlayerSetUpStage.PlayerSetUpHolder.INSTANCE != null ?
                PlayerSetUpStage.PlayerSetUpHolder.INSTANCE :
                (PlayerSetUpStage.PlayerSetUpHolder.INSTANCE = new PlayerSetUpStage());
    }

    /**
     * Closes and clears the singleton instance.
     */
    public static void deleteInstance() {
        if (PlayerSetUpHolder.INSTANCE != null) {
            PlayerSetUpHolder.INSTANCE.close();
            PlayerSetUpHolder.INSTANCE = null;
        }
    }
}