module com.example.nouveau {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.nouveau to javafx.fxml;
    exports com.example.nouveau;
}