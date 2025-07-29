package org.example.eiscuno.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.PlayerSetUpStage;
import org.example.eiscuno.view.StartUnoView;

import java.io.IOException;

public class PlayerSetUpController {

    @FXML
    void goBackToStart(ActionEvent event) throws IOException {
        StartUnoView.getInstance();
        PlayerSetUpStage.deleteInstance();
    }
    @FXML
    void startGame(ActionEvent event) throws IOException {
        GameUnoStage.getInstance();
        PlayerSetUpStage.deleteInstance();
    }


}
