package com.example.nouveau;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * The class {@code HelloApplication} is the entry point of the JavaFX application.
 * It loads the graphical interface defined in the FXML file {@code Homepage.fxml}
 * and displays it in a fixed-size window.
 */
public class HelloApplication extends Application {

    /**
     * This method is called at the start of the JavaFX application.
     * It loads the FXML file, creates the main scene, and displays the window.
     * @param stage the main window (stage) of the application.
     * @throws IOException if the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        //Creation of THE JavaFX scene
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Homepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000,600);
        stage.setTitle("Labyrinthe");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Principal method that launches the JavaFX application.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}
