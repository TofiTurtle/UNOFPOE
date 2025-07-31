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

public class StartUnoController {
    //(boton de salir del juego)
    @FXML
    void exitGame(ActionEvent event) {
        StartUnoView.deleteInstance();
    }
    //creamos variable de plaintext
    private PlainTextFileHandler plainTextFileHandler = new PlainTextFileHandler();
    //creamos var de serializable
    private SerializableFileHandler serializableFileHandler = new SerializableFileHandler();

    //boton para Continuar partida(falta)
    @FXML
    void loadGame(ActionEvent event) throws IOException {
        String[] data = plainTextFileHandler.readFromFile("player_data.csv");
        String playerName = data[0]; // Player name
        String characterImagePath = data[1]; // Character image path

        //deserializamos pa coger los datos
        GameState gameState = (GameState) serializableFileHandler.deserialize("game_data.ser");


        GameUnoStage.getInstance(playerName,characterImagePath, gameState);
        StartUnoView.deleteInstance();
    }

    //boton de ir a playersetup
    @FXML
    void goToPlayerSetUp(ActionEvent event) throws IOException {
        //Ahora, de Start Uno -> PlayerSetUpStage -> Game Uno
        PlayerSetUpStage.getInstance(); //creamos instance de el playersetup
        StartUnoView.deleteInstance(); //borramos la ventana anterior
    }
}
