package com.example.nouveau;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
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

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class HelloController {

    private double zoomFactor = 1.0;
    private Maze labyrinth;
    private Timeline pathTimeline;  // <- pour pouvoir arrêter l'animation



    @FXML private ScrollPane mainPane;
    @FXML private GridPane gridPane;
    @FXML private TextField widthInput;
    @FXML private TextField heightInput;
    @FXML private TextField seedInput;
    @FXML private ChoiceBox<String> MethodGeneration;
    @FXML private ChoiceBox<String> MethodSolve;

    @FXML
    public void initialize() {
        MethodGeneration.setItems(FXCollections.observableArrayList("Parfait", "Imparfait"));
        MethodSolve.setItems(FXCollections.observableArrayList("Tremaux", "HandOnWall", "BFS"));
        MethodGeneration.setValue("Parfait");
    }

    @FXML
    public void GenerateMaze(){
        zoomFactor = 1.0;
        applyZoom();
        gridPane.getChildren().clear(); //Reset le GridPane pour qu'il n'y ait aucune colonne ni ligne
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (node instanceof Pane pane) {
                pane.setStyle("");
            }
        }

        int width, height, seed;

        if (pathTimeline != null) {
            pathTimeline.stop();
            pathTimeline = null;
        }

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
                (cell.getNorth() ? "1 " : "0 ") +
                (cell.getEast() ? "1 " : "0 ") +
                (cell.getSouth() ? "1 " : "0 ") +
                (cell.getWest() ? "1" : "0") + ";");
        return pane;
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

    @FXML
    public void solveMaze() {
        if (labyrinth == null) return;

        Resolve solver = new Resolve();
        List<Case> path = List.of();
        // TODO mettre les algo correspondants
        switch (MethodSolve.getValue()) {
            case "Tremaux":
                //path = solver.Tremaux(labyrinth);
                break;
            case "BFS":
                //path = solver.BFS(labyrinth);
                break;
            case "HandToHand":
            default:
                path = solver.HandOnWall(labyrinth);
                break;
        }
        //drawPath(path);
        showPathStepByStep(path);
    }

    private void drawPath(List<Case> path) {
        if (path == null || path.isEmpty()) return;

        for (Case c : path) {
            Pane cellPane = getPaneFromGrid(c.getY(), c.getX()); // Attention : x = ligne, y = colonne
            if (cellPane != null) {
                cellPane.setStyle(cellPane.getStyle() + "; -fx-background-color: #ff6a00");
            }
        }
    }

    private Pane getPaneFromGrid(int col, int row) {
        for (Node node : gridPane.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);

            if ((nodeCol == null ? 0 : nodeCol) == col && (nodeRow == null ? 0 : nodeRow) == row) {
                return (Pane) node;
            }
        }
        return null;
    }

    public void showPathStepByStep(List<Case> path) {
        if (pathTimeline != null) {
            pathTimeline.stop();  // Stoppe l'ancienne animation si elle tourne
        }

        pathTimeline = new Timeline();
        int delay = 100;

        for (int i = 0; i < path.size(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * delay), event -> {
                Case c = path.get(index);
                Pane cellPane = getPaneFromGrid(c.getY(), c.getX());
                if (cellPane != null) {
                    cellPane.setStyle(cellPane.getStyle() + "-fx-background-color: red;");
                }
            });
            pathTimeline.getKeyFrames().add(keyFrame);
        }
        pathTimeline.play();
    }



}