package org.example.eiscuno.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayerSetUpStage extends Stage {
    /*
    Se crea la vista de inicio con el patron singlenton
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

        // ✅ Pantalla completa
        setFullScreen(true);
        setFullScreenExitHint("");
        setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH); // evita que se salga con tecla

        // ❗ Permitir salir solo si tú lo controlas (opcional)
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE -> PlayerSetUpStage.deleteInstance(); // si quieres permitir ESC
            }
        });

        show();
    }


    private static class PlayerSetUpHolder {
        private static PlayerSetUpStage INSTANCE;

    }

    public static PlayerSetUpStage getInstance() throws IOException {
        return PlayerSetUpStage.PlayerSetUpHolder.INSTANCE != null ?
                PlayerSetUpStage.PlayerSetUpHolder.INSTANCE :
                (PlayerSetUpStage.PlayerSetUpHolder.INSTANCE = new PlayerSetUpStage());
    }

    public static void deleteInstance() {
        if (PlayerSetUpHolder.INSTANCE != null) {
            PlayerSetUpHolder.INSTANCE.close();
            PlayerSetUpHolder.INSTANCE = null;
        }
    }
}