package org.example.eiscuno.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Singleton Stage for the UNO game start screen.
 * <p>
 * Implements the singleton pattern to ensure only one instance exists.
 * Manages the initial game view with full-screen display configuration.
 * </p>
 */
public class StartUnoView extends Stage {

    /**
     * Constructs the UNO start view stage.
     * <p>
     * Loads the FXML view, configures full-screen properties, and sets up key listeners.
     * The stage cannot be exited accidentally by keyboard shortcuts except through
     * programmed ESC key handling.
     * </p>
     *
     * @throws IOException if there's an error loading the FXML file
     */
    public StartUnoView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eiscuno/StartView.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new IOException("Error al cargar el archvio FXML", e);
        }
        Scene scene = new Scene(root);
        setTitle("EISC Uno");
        setScene(scene);

        // Configure full-screen properties
        setFullScreen(true);
        setFullScreenExitHint("");
        setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
        show();

        // Set up ESC key listener for controlled exit
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE -> StartUnoView.deleteInstance();
            }
        });
    }

    /**
     * Holder class for the singleton instance (Bill Pugh pattern).
     */
    private static class StartUnoViewHolder {
        private static StartUnoView INSTANCE;
    }

    /**
     * Gets the singleton instance of the start view.
     *
     * @return the singleton instance
     * @throws IOException if there's an error creating the stage
     */
    public static StartUnoView getInstance() throws IOException {
        return StartUnoViewHolder.INSTANCE != null ?
                StartUnoViewHolder.INSTANCE :
                (StartUnoViewHolder.INSTANCE = new StartUnoView());
    }

    /**
     * Closes and clears the singleton instance.
     */
    public static void deleteInstance() {
        if (StartUnoViewHolder.INSTANCE != null) {
            StartUnoViewHolder.INSTANCE.close();
            StartUnoViewHolder.INSTANCE = null;
        }
    }
}