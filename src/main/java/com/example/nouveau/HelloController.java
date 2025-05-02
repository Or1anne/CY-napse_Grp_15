package com.example.nouveau;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.HashSet;
import java.util.Random;

public class HelloController {

    private double zoomFactor = 1.0;

    @FXML private Pane mainPane;
    @FXML private GridPane gridPane;
    @FXML private TextField widthInput;
    @FXML private TextField heightInput;
    @FXML private TextField seedInput;
    @FXML private ChoiceBox<String> MethodGeneration;
    @FXML private ChoiceBox<String> MethodSolve;

    @FXML
    public void initialize() {
        MethodGeneration.setItems(FXCollections.observableArrayList("Parfait", "Imparfait"));
        MethodSolve.setItems(FXCollections.observableArrayList("Tremaux", "HandToHand", "BFS"));
        MethodGeneration.setValue("Parfait");
    }

    @FXML
    public void GenerateMaze(){
        gridPane.getChildren().clear(); //Reset le GridPane pour qu'il n'y ait aucune colonne ni ligne
        int width, height, seed;
        try {
            width = Integer.parseInt(widthInput.getText());
            height = Integer.parseInt(heightInput.getText());
        } catch(NumberFormatException e) {
            width = 30;
            height = 30;
        }

        try{
            seed = Integer.parseInt(seedInput.getText());
        }catch(NumberFormatException e){
            seed = new Random().nextInt();
        }

        Maze labyrinth = new Maze(width, height);
        if(MethodGeneration.getValue().equals("Parfait")){
            labyrinth.KruskalGeneration(seed);
        }
        else{
            labyrinth.KruskalImperfectGeneration(seed);
        }

        double maxDisplayWidth = mainPane.getWidth();
        double maxDisplayHeight = mainPane.getHeight();
        double cellWidth = maxDisplayWidth / width;
        double cellHeight = maxDisplayHeight / height;
        double cellSize = Math.min(cellWidth, cellHeight);

        double gridWidth = width * cellSize;
        double gridHeight = height * cellSize;

        // Définir la taille du GridPane
        gridPane.setPrefSize(gridWidth, gridHeight);

        // Centrer le GridPane
        gridPane.setLayoutX((maxDisplayWidth - gridWidth) / 2);
        gridPane.setLayoutY((maxDisplayHeight - gridHeight) / 2);

        for(int i=0; i<labyrinth.getHeight(); i++){
            for(int j=0; j< labyrinth.getWidth(); j++){
                Case cell = labyrinth.getMaze()[i][j];
                Pane pane = createCellPane(cell, cellSize);
                gridPane.add(pane,j,i);
            }
        }
    }

    private Pane createCellPane(Case cell, double cellSize) {
        Pane pane = new Pane();
        pane.setPrefSize(cellSize, cellSize);
        pane.setStyle("-fx-border-color: black; -fx-border-width: " +
                (cell.North ? "1 " : "0 ") +
                (cell.East ? "1 " : "0 ") +
                (cell.South ? "1 " : "0 ") +
                (cell.West ? "1" : "0") + ";");
        return pane;
    }

    @FXML
    void MazeZoom(ScrollEvent event) {
        // Si la molette de la souris est tournée vers le haut (zoom avant)
        if (event.getDeltaY() > 0) {
            zoomFactor *= 1.1;  // Zoom in
        }
        // Si la molette de la souris est tournée vers le bas (zoom arrière)
        else {
            zoomFactor /= 1.1;  // Zoom out
        }
        applyZoom();
        event.consume();  // Empêche la propagation de l'événement
    }
    private void applyZoom() {
        gridPane.setScaleX(zoomFactor);
        gridPane.setScaleY(zoomFactor);
        }

    public void MazeNavigate(MouseEvent mouseEvent) {
    }
}