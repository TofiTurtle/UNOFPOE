module org.example.eiscuno {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    // Si usas clases de javafx.base, agrega también:
    requires javafx.base;

    exports org.example.eiscuno;
    opens org.example.eiscuno to javafx.fxml;
    opens org.example.eiscuno.controller to javafx.fxml;

}


