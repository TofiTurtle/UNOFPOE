package org.example.eiscuno.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.eiscuno.model.saveGame.PlainTextFileHandler;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.PlayerSetUpStage;
import org.example.eiscuno.view.StartUnoView;

import java.io.IOException;

public class StartUnoController {
    @FXML
    private Button buttonContinue;

    @FXML
    private Button buttonExit;

    @FXML
    private Button buttonStart;

    //(boton de salir del juego)
    @FXML
    void exitGame(ActionEvent event) {
        StartUnoView.deleteInstance();
    }
    //creamos variable de plaintext
    private PlainTextFileHandler plainTextFileHandler = new PlainTextFileHandler();


    //boton para Continuar partida(falta)
    @FXML
    void loadGame(ActionEvent event) throws IOException {
        String[] data = plainTextFileHandler.readFromFile("player_data.csv");
        String playerName = data[0]; // Player name
        String characterImagePath = data[1]; // Character image path


        GameUnoStage.getInstance(playerName,characterImagePath);
    }

    //boton de ir a playersetup
    @FXML
    void goToPlayerSetUp(ActionEvent event) throws IOException {
        //Antes, Start Uno -> Gameuno:
        //GameUnoStage.getInstance();
        //StartUnoView.deleteInstance();

        //Ahora, de Start Uno -> PlayerSetUpStage -> Game Uno
        PlayerSetUpStage.getInstance(); //creamos instance de el playersetup
        StartUnoView.deleteInstance(); //borramos la ventana anterior
    }
}
