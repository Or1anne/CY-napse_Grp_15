module com.example.nouveau {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.nouveau to javafx.fxml;
    exports com.example.nouveau;
}