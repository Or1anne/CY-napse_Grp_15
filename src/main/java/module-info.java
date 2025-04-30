module com.example.cynapse {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cynapse to javafx.fxml;
    exports com.example.cynapse;
}