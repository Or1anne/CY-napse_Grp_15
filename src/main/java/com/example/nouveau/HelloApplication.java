package com.example.nouveau;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * La classe {@code HelloApplication} est le point d'entrée de l'application JavaFX.
 * Elle charge l'interface graphique définie dnas le fichier FXML {@code Homepage.fxml}
 * et l'affiche dans une fenêtre de taille fixe.
 */
public class HelloApplication extends Application {

    /**
     * Méthode appelée au démarrage de l'application JavaFX.
     * Elle charge le fichier FXML, crée la scène pricipale et affiche la fenêtre.
     * @param stage la fenêtre principale (stage) de l'application.
     * @throws IOException si le fichier FXML ne peut pas être chargé.
     */
    @Override
    public void start(Stage stage) throws IOException {
        //Création Scene JAVAFX
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Homepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000,600);
        stage.setTitle("Labyrinthe");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Méthode principale qui lance l'application JavaFX.
     * @param args les arguments de la lignes de commande.
     */
    public static void main(String[] args) {
        launch();
    }
}
