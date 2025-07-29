package org.example.eiscuno.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayerSetUp extends Stage {
    /*
    Se crea la vista de inicio con el patron singlenton
     */

    public PlayerSetUp() throws IOException {
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
        show();

    }

    private static class PlayerSetUpHolder {
        private static PlayerSetUp INSTANCE;

    }
    public static PlayerSetUp getInstance() throws IOException {
        return PlayerSetUp.PlayerSetUpHolder.INSTANCE != null ?
                PlayerSetUp.PlayerSetUpHolder.INSTANCE :
                (PlayerSetUp.PlayerSetUpHolder.INSTANCE = new PlayerSetUp());
    }
    public static void deleteInstance() {
        PlayerSetUpHolder.INSTANCE.close();
        PlayerSetUpHolder.INSTANCE = null;
    }


}