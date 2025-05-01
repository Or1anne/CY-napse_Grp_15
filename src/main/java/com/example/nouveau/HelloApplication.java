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
        Maze labyrinthe = new Maze(10, 10); // crée un labyrinthe 10x10
        labyrinthe.generation();
        Canvas canvas = new Canvas(400, 400); // Spécifie une taille
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawLabyrinthe(gc, labyrinthe);

        //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        Scene scene = new Scene(/*fxmlLoader.load()*/ root, 400, 400);
        stage.setTitle("Labyrinthe");
        stage.setScene(scene);
        stage.show();
    }

    private void drawLabyrinthe(GraphicsContext gc, Maze labyrinthe) {
        double cellSize = 40;
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
    }

    public static void main(String[] args) {
        launch();
    }
}