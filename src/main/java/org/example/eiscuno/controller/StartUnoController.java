package org.example.eiscuno.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.StartUnoView;

import java.io.IOException;

public class StartUnoController {
    @FXML
    private Button buttonContinue;

    @FXML
    private Button buttonExit;

    @FXML
    private Button buttonStart;

    @FXML
    void exitGame(ActionEvent event) {
        StartUnoView.deleteInstance();
    }

    @FXML
    void loadGame(ActionEvent event) {

    }

    @FXML
    void startGame(ActionEvent event) throws IOException {
        GameUnoStage.getInstance();
        StartUnoView.deleteInstance();
    }
}
