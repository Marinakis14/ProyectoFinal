module com.example.proyectofinal {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens com.example.proyectofinal to javafx.fxml;
    exports com.example.proyectofinal;
}