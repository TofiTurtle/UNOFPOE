package org.example.eiscuno.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartUnoView extends Stage {
    /*
    Se crea la vista de inicio con el patron singlenton
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
        setFullScreen(true); // ✅ Pantalla completa
        setFullScreenExitHint(""); // ❌ No mostrar mensaje de "Presione ESC para salir"
        setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH); // ❌ Desactiva salir con tecla
        show();

        // ✅ Pero nosotros escuchamos la tecla ESC para cerrar (si se desea)
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE -> {
                    // Comentar esta línea si no quieres que Escape cierre
                    StartUnoView.deleteInstance();
                }
            }
        });

    }

    private static class StartUnoViewHolder {
        private static StartUnoView INSTANCE;

    }

    public static StartUnoView getInstance() throws IOException {
        return StartUnoView.StartUnoViewHolder.INSTANCE != null ?
                StartUnoView.StartUnoViewHolder.INSTANCE :
                (StartUnoView.StartUnoViewHolder.INSTANCE = new StartUnoView());
    }

    public static void deleteInstance() {
        if (StartUnoViewHolder.INSTANCE != null) {
            StartUnoViewHolder.INSTANCE.close();
            StartUnoViewHolder.INSTANCE = null;
        }
    }
}