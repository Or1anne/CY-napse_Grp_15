package com.example.nouveau;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;

public class HelloController {

    public Maze labyrinth;
    public Database db;
    private double zoomFactor = 1.0;

    @FXML private ScrollPane mainPane;
    @FXML private GridPane gridPane;
    @FXML private TextField widthInput;
    @FXML private TextField heightInput;
    @FXML private TextField MazeName;
    @FXML private TextField seedInput;
    @FXML private ChoiceBox<String> MethodGeneration;
    @FXML private ChoiceBox<String> MethodSolve;
    @FXML private ChoiceBox<String> SaveList;

    @FXML
    public void initialize() throws SQLException {
        db = new Database();
        db.createDatabase();
        db.createTable();
        MethodGeneration.setItems(FXCollections.observableArrayList("Parfait", "Imparfait"));
        MethodSolve.setItems(FXCollections.observableArrayList("Tremaux", "HandToHand", "BFS"));
        SaveList.setItems(db.getMazeList());
        MethodGeneration.setValue("Parfait");
    }

    @FXML
    public void GenerateMaze(){
        zoomFactor = 1.0;
        applyZoom();
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

        labyrinth = new Maze(width, height);
        if(MethodGeneration.getValue().equals("Parfait")){
            labyrinth.KruskalGeneration(seed);
        }
        else{
            labyrinth.KruskalImperfectGeneration(seed);
        }

        double cellWidth = mainPane.getWidth() / width;
        double cellHeight = mainPane.getHeight() / height;
        double cellSize = Math.min(cellWidth, cellHeight);

        gridPane.setPrefSize(width * cellSize, height * cellSize);

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
    public void SaveMaze(){
        String Name;
        try{
            Name = MazeName.getText();
        }catch(NumberFormatException e){
            Name = "Labyrinthe";
        }
        db.SaveMaze(labyrinth, Name);
    }
    
    @FXML
    void MazeZoom(ScrollEvent event) {
        if (event.getDeltaY() > 0) {
            zoomFactor *= 1.1;  // Zoom in
        }
        else {
            zoomFactor /= 1.1;  // Zoom out
        }
        applyZoom();
    }
    private void applyZoom() {
        gridPane.setScaleX(zoomFactor);
        gridPane.setScaleY(zoomFactor);
    }

    @FXML
    public void MousePressed(MouseEvent mouseEvent) {
        double mouseX = mouseEvent.getSceneX();
        double mouseY = mouseEvent.getSceneY();
        mainPane.setCursor(Cursor.CLOSED_HAND);
    }

    @FXML
    public void MouseReleased(MouseEvent mouseEvent) {
        mainPane.setCursor(Cursor.DEFAULT);
    }

}