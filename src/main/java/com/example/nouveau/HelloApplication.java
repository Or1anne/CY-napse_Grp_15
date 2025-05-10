package com.example.nouveau;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        int width = 30;
        int height = 30;
        double cellSize = 20;

        Maze labyrinthe = new Maze(width, height); // crée un labyrinthe 10x10
        labyrinthe.KruskalGeneration();
        Resolve Resolve = new Resolve(labyrinthe);
        ArrayList<Case> path = Resolve.Tremaux();
        Canvas canvas = new Canvas(width*cellSize+1, height*cellSize+1); // Spécifie une taille
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawLabyrinthe(gc, labyrinthe, cellSize);

        //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        Scene scene = new Scene(/*fxmlLoader.load()*/ root, width*cellSize+10, height*cellSize+10);
        stage.setTitle("Labyrinthe");
        stage.setScene(scene);
        stage.show();
    }

    private void drawLabyrinthe(GraphicsContext gc, Maze labyrinthe, double cellSize) {
        Case[][] grid = labyrinthe.getMaze();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                double xPos = x * cellSize;
                double yPos = y * cellSize;
                Case c = grid[y][x];

                if (c.North) gc.strokeLine(xPos, yPos, xPos + cellSize, yPos);
                if (c.South) gc.strokeLine(xPos, yPos + cellSize, xPos + cellSize, yPos + cellSize);
                if (c.West) gc.strokeLine(xPos, yPos, xPos, yPos + cellSize);
                if (c.East) gc.strokeLine(xPos + cellSize, yPos, xPos + cellSize, yPos + cellSize);
            }
        }

        gc.setFill(javafx.scene.paint.Color.color(1, 0, 0, 0.5)); // rouge semi-transparent
        for (Case c : path) {
            double xPos = c.getY() * cellSize; // y = largeur
            double yPos = c.getX() * cellSize; // x = hauteur
            gc.fillRect(xPos + 1, yPos + 1, cellSize - 2, cellSize - 2); // petit carré rouge
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
