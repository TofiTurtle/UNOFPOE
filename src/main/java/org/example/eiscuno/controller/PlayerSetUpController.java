package org.example.eiscuno.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.PlayerSetUpStage;
import org.example.eiscuno.view.StartUnoView;

import java.io.IOException;
import java.util.List;

public class PlayerSetUpController {
    @FXML
    private ImageView imageView;
    @FXML
    private TextField textField;
    @FXML
    private Label emptyNameLabel;
    private List<Image> images;
    private int currentIndex = 2;

    @FXML
    public void initialize() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                emptyNameLabel.setVisible(false); // mira si esta vacio para mostrar el mensaje de trin
            }
        });

        images = List.of(
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/cards-uno/skip_blue.png")),
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/cards-uno/skip_green.png")),
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/cards-uno/skip_yellow.png")),
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/cards-uno/skip_red.png"))
        );
        imageView.setImage(images.get(currentIndex));

    }

    @FXML
    private void nextImage() {
        if (currentIndex < images.size() - 1) {
            currentIndex++;
            imageView.setImage(images.get(currentIndex));
        } else {
            currentIndex = 0;
            imageView.setImage(images.get(currentIndex));
        }
    }

    @FXML
    private void previousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            imageView.setImage(images.get(currentIndex));

        }else {
            currentIndex = images.size() - 1;
            imageView.setImage(images.get(currentIndex));
        }
    }




    //metodos existentes de botones iniciales
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
