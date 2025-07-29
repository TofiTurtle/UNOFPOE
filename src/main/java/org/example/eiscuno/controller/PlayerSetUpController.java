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
/*Ojo vivo, necesitaremos algo asi luego en el controlador
    private String PathListImages[] = { "/com/example/batallanavalfpoe/images/character1.PNG", "/com/example/batallanavalfpoe/images/character2.PNG", "/com/example/batallanavalfpoe/images/character3.PNG",
            "/com/example/batallanavalfpoe/images/character4.PNG","/com/example/batallanavalfpoe/images/character5.PNG","/com/example/batallanavalfpoe/images/character6.PNG", "/com/example/batallanavalfpoe/images/character7.PNG"
    };

 */
    @FXML //que este codigo se ejecute siempre cuando se inice
    public void initialize() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                emptyNameLabel.setVisible(false); // mira si esta vacio para mostrar el mensaje de ingresar nombre
            }
        });
        //Lista que contiene las imagenes disponibles (falta agregarlas)
        images = List.of(
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/cards-uno/skip_blue.png")),
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/cards-uno/skip_green.png")),
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/cards-uno/skip_yellow.png")),
                new Image(getClass().getResourceAsStream("/org/example/eiscuno/cards-uno/skip_red.png"))
        );
        imageView.setImage(images.get(currentIndex)); //iniiclamente se pone una imagen x cualquiera

    }

    //Metodos para cambiar de imagen y asi el jugador pueda escoger la que desee
    @FXML
    private void nextImage() {
        if (currentIndex < images.size() - 1) {  //si es menor que el tamaño-1 (recordar listas van desde 0)
            currentIndex++; //suma a la variable currenIndex (imgen que se muestra actualmente=
            imageView.setImage(images.get(currentIndex)); //se pone visualmente
        } else { //si NO es menor (osea, es igual a 3), ya esta en la cola, por lo que con el siguiente click
            currentIndex = 0; //pasa a la cabeza de la lsita de imagenes.
            imageView.setImage(images.get(currentIndex)); //se pone visualmente
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




    //metodos existentes de botones iniciales-------------------
    @FXML
    void goBackToStart(ActionEvent event) throws IOException {
        StartUnoView.getInstance();
        PlayerSetUpStage.deleteInstance();
    }
    @FXML
    void startGame(ActionEvent event) throws IOException {
        //Para el gameUnostage, le pasaremos el nombre y la imagen que se escogio
        String name = textField.getText().trim();
        int currentImageIndex = currentIndex;
        //se los pasamos al gmunostage
        GameUnoStage.getInstance(name,images.get(currentIndex));
        PlayerSetUpStage.deleteInstance();
    }






}
