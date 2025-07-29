package org.example.eiscuno.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.PlayerSetUp;
import org.example.eiscuno.view.StartUnoView;

import java.io.IOException;

public class PlayerSetUpController {

    @FXML
    void goBackToStart(ActionEvent event) throws IOException {
        StartUnoView.getInstance();
        PlayerSetUp.deleteInstance();
    }
    @FXML
    void startGame(ActionEvent event) throws IOException {
        GameUnoStage.getInstance();
        PlayerSetUp.deleteInstance();
    }


}
