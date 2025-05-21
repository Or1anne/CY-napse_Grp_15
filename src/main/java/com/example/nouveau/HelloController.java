package com.example.nouveau;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

import javafx.scene.input.MouseEvent;

import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;



import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import java.io.IOException;
import java.util.*;


import javafx.stage.Stage;
import javafx.util.Duration;

public class HelloController {

    private double zoomFactor = 1.0;
    private Maze currentMaze;
    private Timeline pathTimeline;
    private Timeline generationTimeline;
    private ProgressBar progressBar;
    private Database db;
    private Image wallHorizontal = new Image(getClass().getResourceAsStream("/com/example/nouveau/wall_horizontal.png"));
    private Image wallVertical = new Image(getClass().getResourceAsStream("/com/example/nouveau/wall_vertical.png"));
    private Image wallCorner = new Image(getClass().getResourceAsStream("/com/example/nouveau/wall_corner.png"));
    private double wallThicknessRatio = 0.25;
    private boolean editMode = false;


    @FXML
    private ScrollPane mainPane;
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField widthInput;
    @FXML
    private TextField heightInput;
    @FXML
    private TextField MazeName;
    @FXML
    private TextField seedInput;
    @FXML
    private TextField SpeedInputGeneration;
    @FXML
    private ToggleGroup MethodGeneration;
    @FXML
    private ToggleGroup MethodSolve;
    @FXML
    private Button editModeButton;
    @FXML
    private ToggleButton toggleSwitch;
    @FXML
    private StackPane MazeStackPane;
    @FXML
    private ToggleButton toggleSwitchResolve;
    @FXML
    private TextField SpeedInputResolve;
    @FXML
    private Button SaveButton;
    @FXML
    private Button ModeEdition;


    @FXML
    public void initialize() {
        SpeedInputGeneration.setVisible(false);
        SpeedInputGeneration.setManaged(false);
        SpeedInputResolve.setVisible(false);
        SpeedInputResolve.setManaged(false);
        ModeEdition.setDisable(true);
        SaveButton.setDisable(true);
    }


    @FXML
    private void handleToggle() {
        if (toggleSwitch.isSelected()) {
            toggleSwitch.setText("Désactiver");
            SpeedInputGeneration.setVisible(true);
            SpeedInputGeneration.setManaged(true);
        } else {
            toggleSwitch.setText("Activer");
            SpeedInputGeneration.setVisible(false);
            SpeedInputGeneration.setManaged(false);
            SpeedInputGeneration.setText(null);
        }
    }

    @FXML
    private void handleToggleResolve() {
        if (toggleSwitchResolve.isSelected()) {
            toggleSwitchResolve.setText("Désactiver");
            SpeedInputResolve.setVisible(true);
            SpeedInputResolve.setManaged(true);
        } else {
            toggleSwitchResolve.setText("Activer");
            SpeedInputResolve.setVisible(false);
            SpeedInputResolve.setManaged(false);
            SpeedInputResolve.setText(null);
        }
    }

    @FXML
    public void GenerateMaze() {

        if (editMode) {
            showError("Mode édition", "La résolution est désactivée en mode édition.");
            return;
        }

        if (generationTimeline != null) {
            generationTimeline.stop();
            generationTimeline = null;
        }
        if (progressBar != null) {
            MazeStackPane.getChildren().remove(progressBar);
            progressBar = null;
        }
        if (pathTimeline != null) {
            pathTimeline.stop();
            pathTimeline = null;
        }
        SaveButton.setDisable(true);
        ModeEdition.setDisable(true);


        zoomFactor = 1.0;
        applyZoom();
        gridPane.getChildren().clear();
        int width, height, seed;
        try {
            width = Integer.parseInt(widthInput.getText());
            height = Integer.parseInt(heightInput.getText());

            // Limites de génération 100 * 100
            if (width < 1 || height < 1 || width > 100 || height > 100) {
                showError("Taille invalide", "La taille doit être entre 1x1 et 100x100");
                return;
            }
        } catch (NumberFormatException e) {
            width = 20;
            height = 20;
        }

        try {
            seed = Integer.parseInt(seedInput.getText());
        } catch (NumberFormatException e) {
            seed = new Random().nextInt();
        }

        LinkedList<int[]> steps;
        currentMaze = new Maze(width, height);
        RadioButton SelectMethod = (RadioButton) MethodGeneration.getSelectedToggle();
        if (SelectMethod.getText().equals("Parfait")) {
            steps = currentMaze.KruskalGeneration(seed);
        } else {
            steps = currentMaze.KruskalImperfectGeneration(seed);
        }
        double cellWidth = mainPane.getWidth() / width;
        double cellHeight = mainPane.getHeight() / height;
        double cellSize = Math.min(cellWidth, cellHeight);

        gridPane.setPrefSize(width * cellSize, height * cellSize);

        for (int i = 0; i < currentMaze.getHeight(); i++) {
            for (int j = 0; j < currentMaze.getWidth(); j++) {
                Case cell = currentMaze.getMaze()[i][j];
                Pane pane = createCellPane(cell, cellSize);
                gridPane.add(pane, j, i);
            }
        }
        if (toggleSwitch.isSelected()) {
            progressBar = new ProgressBar(0);
            progressBar.setMaxWidth(width * cellSize / 2);
            progressBar.prefHeight(50);
            MazeStackPane.getChildren().add(progressBar);
            double totalSteps = steps.size();
            int speed = (SpeedInputGeneration.getText() == null || SpeedInputGeneration.getText().isEmpty()) ? 10 : Integer.parseInt(SpeedInputGeneration.getText());
            generationTimeline = new Timeline();
            KeyFrame keyFrame = new KeyFrame(Duration.millis(speed), event -> {
                if (!steps.isEmpty()) {
                    int[] wall = steps.poll();
                    if (SelectMethod.getText().equals("Parfait")) {
                        currentMaze.setWallsPerfect(wall, currentMaze.getMaze()[wall[0]][wall[1]], currentMaze.getMaze()[wall[2]][wall[3]]);
                    } else {
                        currentMaze.setWallsPerfectImperfect(wall, currentMaze.getMaze()[wall[0]][wall[1]], currentMaze.getMaze()[wall[2]][wall[3]]);
                    }
                    redrawMaze();
                    progressBar.setProgress(1 - ((double) steps.size() / totalSteps));
                }
            });
            generationTimeline.getKeyFrames().add(keyFrame);
            generationTimeline.setCycleCount(steps.size());
            generationTimeline.play();
            generationTimeline.setOnFinished(e -> {
                MazeStackPane.getChildren().remove(progressBar);
                SaveButton.setDisable(false);
                ModeEdition.setDisable(false);
            });
        } else {
            while (!steps.isEmpty()) {
                int[] wall = steps.poll();
                if (SelectMethod.getText().equals("Parfait")) {
                    currentMaze.setWallsPerfect(wall, currentMaze.getMaze()[wall[0]][wall[1]], currentMaze.getMaze()[wall[2]][wall[3]]);
                } else {
                    currentMaze.setWallsPerfectImperfect(wall, currentMaze.getMaze()[wall[0]][wall[1]], currentMaze.getMaze()[wall[2]][wall[3]]);
                }
            }
            redrawMaze();
            SaveButton.setDisable(false);
            ModeEdition.setDisable(false);
        }
    }

    @FXML
    public void resetMaze() {
        // on stoppe l'animation du serpent
        if (pathTimeline != null) {
            pathTimeline.stop();
            pathTimeline = null;
        }


        redrawMaze();
    }

    @FXML
    private void toggleEditMode() {
        editMode = !editMode;
        if (editMode) {
            editModeButton.setText("Mode édition : ON");
            setControlsDisabled(true);

            // Arrête l'animation en cours si elle existe
            if (pathTimeline != null) {
                pathTimeline.stop();
                pathTimeline = null;
            }

        } else {
            editModeButton.setText("Mode édition : OFF");
            setControlsDisabled(false);
            validateMaze();
        }
    }

    private void validateMaze() {
        // Par exemple, on pourrait sauvegarder automatiquement le labyrinthe ou juste afficher un message
        System.out.println("Labyrinthe validé !");
        // ou tu peux appeler ta méthode de sauvegarde, ou juste redessiner sans édition possible
        redrawMaze();
    }


    private Pane createCellPane(Case cell, double cellSize) {
        Pane pane = new Pane();
        pane.setPrefSize(cellSize, cellSize);
        pane.setStyle("-fx-background-color: white;");

        // Mur en image (pierre)
        /*pane.getChildren().clear();

        double wallThickness = Math.max(2, cellSize * wallThicknessRatio);

        int x = cell.getX(); // ligne
        int y = cell.getY(); // colonne
        int width = currentMaze.getWidth();
        int height = currentMaze.getHeight();

        if(cell.getNorth()) {
            ImageView northWall = new ImageView(wallHorizontal);
            northWall.setFitWidth(cellSize);
            northWall.setFitHeight(wallThickness);
            northWall.setLayoutX(0);
            northWall.setLayoutY(0);
            pane.getChildren().add(northWall);
        }
        if(cell.getSouth()) {
            ImageView southWall = new ImageView(wallHorizontal);
            southWall.setFitWidth(cellSize);
            southWall.setFitHeight(wallThickness);
            southWall.setLayoutX(0);
            southWall.setLayoutY(cellSize - 5);
            pane.getChildren().add(southWall);
        }
        if(cell.getWest()) {
            ImageView westWall = new ImageView(wallVertical);
            westWall.setFitWidth(wallThickness);
            westWall.setFitHeight(cellSize);
            westWall.setLayoutX(0);
            westWall.setLayoutY(0);
            pane.getChildren().add(westWall);
        }
        if(cell.getEast()) {
            ImageView eastWall = new ImageView(wallVertical);
            eastWall.setFitWidth(wallThickness);
            eastWall.setFitHeight(cellSize);
            eastWall.setLayoutX(cellSize - 5);
            eastWall.setLayoutY(0);
            pane.getChildren().add(eastWall);
        }*/


        // mur en trait
        pane.setStyle("-fx-border-color: black; -fx-border-width: " +
                (cell.getNorth() ? "1 " : "0 ") +
                (cell.getEast() ? "1 " : "0 ") +
                (cell.getSouth() ? "1 " : "0 ") +
                (cell.getWest() ? "1" : "0") + ";");


        pane.setOnMouseClicked(event -> {
            if (!editMode) {
                return; // pas en mode édition
            }
            int x = cell.getX();
            int y = cell.getY();
            double clickX = event.getX();
            double clickY = event.getY();

            double margin = cellSize * 0.2;

            // Murs extérieurs : contour
            boolean isBorderWall = (x == 0 && clickY < margin) || // Nord extérieur
                    (x == currentMaze.getHeight() - 1 && clickY > cellSize - margin) || // Sud extérieur
                    (y == 0 && clickX < margin) || // Ouest extérieur
                    (y == currentMaze.getWidth() - 1 && clickX > cellSize - margin); // Est extérieur

            if (isBorderWall) {
                showError("Mur extérieur", "Les murs extérieurs ne peuvent pas être modifiés");
                return;
            }


            if (clickY < margin) {
                boolean current = cell.getNorth();
                cell.setNorth(!current);
                if (x > 0) {
                    currentMaze.getMaze()[x - 1][y].setSouth(!current);
                }
            } else if (clickY > cellSize - margin) {
                boolean current = cell.getSouth();
                cell.setSouth(!current);
                if (x < currentMaze.getHeight() - 1) {
                    currentMaze.getMaze()[x + 1][y].setNorth(!current);
                }
            } else if (clickX < margin) {
                boolean current = cell.getWest();
                cell.setWest(!current);
                if (y > 0) {
                    currentMaze.getMaze()[x][y - 1].setEast(!current);
                }
            } else if (clickX > cellSize - margin) {
                boolean current = cell.getEast();
                cell.setEast(!current);
                if (y < currentMaze.getWidth() - 1) {
                    currentMaze.getMaze()[x][y + 1].setWest(!current);
                }
            }

            redrawMaze();
        });

        return pane;
    }

    private void redrawMaze() {
        if (currentMaze == null) return;

        for (int i = 0; i < currentMaze.getHeight(); i++) {
            for (int j = 0; j < currentMaze.getWidth(); j++) {
                Case cell = currentMaze.getMaze()[i][j];
                Pane pane = (Pane) getNodeFromGridPane(gridPane, j, i);

                if (pane != null) {

                    pane.setStyle("-fx-background-color: white;");

                    //Mur en image (pierre)
                    /*pane.getChildren().clear();
                    double wallThickness = Math.max(2, pane.getPrefWidth() * wallThicknessRatio);

                    if(cell.getNorth()) {
                        ImageView northWall = new ImageView(wallHorizontal);
                        northWall.setFitWidth(pane.getPrefWidth());
                        northWall.setFitHeight(wallThickness);
                        northWall.setLayoutX(0);
                        northWall.setLayoutY(0);
                        pane.getChildren().add(northWall);
                    }
                    if(cell.getSouth()) {
                        ImageView southWall = new ImageView(wallHorizontal);
                        southWall.setFitWidth(pane.getPrefWidth());
                        southWall.setFitHeight(wallThickness);
                        southWall.setLayoutX(0);
                        southWall.setLayoutY(pane.getPrefHeight() - 5);
                        pane.getChildren().add(southWall);
                    }
                    if(cell.getWest()) {
                        ImageView westWall = new ImageView(wallVertical);
                        westWall.setFitWidth(wallThickness);
                        westWall.setFitHeight(pane.getPrefHeight());
                        westWall.setLayoutX(0);
                        westWall.setLayoutY(0);
                        pane.getChildren().add(westWall);
                    }
                    if(cell.getEast()) {
                        ImageView eastWall = new ImageView(wallVertical);
                        eastWall.setFitWidth(wallThickness);
                        eastWall.setFitHeight(pane.getPrefHeight());
                        eastWall.setLayoutX(pane.getPrefWidth() - 5);
                        eastWall.setLayoutY(0);
                        pane.getChildren().add(eastWall);
                    }*/

                    // Mur en trait
                    pane.setStyle("-fx-border-color: black; -fx-border-width: " +
                            (cell.getNorth() ? "1 " : "0 ") +
                            (cell.getEast() ? "1 " : "0 ") +
                            (cell.getSouth() ? "1 " : "0 ") +
                            (cell.getWest() ? "1" : "0") + ";");


                }
            }
        }
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);
            if (nodeCol != null && nodeRow != null && nodeCol == col && nodeRow == row) {
                return node;
            }
        }
        return null;
    }

    @FXML
    void MazeZoom(ScrollEvent event) {
        if (event.getDeltaY() > 0) {
            zoomFactor *= 1.1;
        } else {
            zoomFactor /= 1.1;
        }
        applyZoom();
    }

    private void applyZoom() {
        gridPane.setScaleX(zoomFactor);
        gridPane.setScaleY(zoomFactor);
    }

    @FXML
    public void MousePressed() {
        mainPane.setCursor(Cursor.CLOSED_HAND);
    }

    @FXML
    public void ChooseMaze() {
        if (editMode) {
            showError("Mode édition", "La résolution est désactivée en mode édition.");
            return;
        }

        if (currentMaze == null) return;
        if (pathTimeline != null) {
            pathTimeline.stop();
            pathTimeline = null;
        }
        if (generationTimeline != null) {
            generationTimeline.stop();
            generationTimeline = null;
        }
        if (progressBar != null) {
            MazeStackPane.getChildren().remove(progressBar);
            progressBar = null;
        }


        redrawMaze();
        Resolve solver = new Resolve(currentMaze);
        List<Case> path = new ArrayList<>();
        RadioButton SolveMethod = (RadioButton) MethodSolve.getSelectedToggle();
        switch (SolveMethod.getText()) {
            case "Tremaux":
                path = solver.Tremaux();
                break;
            case "BFS":
                path = solver.BFS();
                break;
            case "Hand on Wall":
                path = solver.HandOnWall();
                break;
            default:
                return;
        }
        if (path == null || path.isEmpty()) {
            showError("Labyrinthe insoluble", "Aucun chemin n’a été trouvé.\nVérifie que l’entrée et la sortie sont accessibles.");
            return;
        }
        if (toggleSwitchResolve.isSelected()) {
            showPathStepByStep(path);
        } else {
            drawPath(path);
        }
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

        if (editMode) {
            if (pathTimeline != null) {
                pathTimeline.stop();
                pathTimeline = null;
            }
        }

        if (pathTimeline != null) {
            pathTimeline.stop();
        }


        int speed = (SpeedInputResolve.getText() == null || SpeedInputResolve.getText().isEmpty()) ? 100 : Integer.parseInt(SpeedInputResolve.getText());
        pathTimeline = new Timeline();

        for (int i = 0; i < path.size(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * speed), event -> {
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

    public static Maze generateMaze(int width, int height, String method, Integer seedOpt) {
        int seed = (seedOpt != null) ? seedOpt : new Random().nextInt();
        Maze maze = new Maze(width, height);
        if ("Parfait".equalsIgnoreCase(method)) {
            maze.KruskalGeneration(seed);
        } else {
            maze.KruskalImperfectGeneration(seed);
        }
        return maze;
    }

    public static void printTerminal(Maze maze) {
        int width = maze.getWidth();
        int height = maze.getHeight();
        Case[][] grill = maze.getMaze();
        boolean[][] isOnPath = new boolean[height][width];

        for (int x = 0; x < width; x++) {
            System.out.print("┌───");
        }
        System.out.println("┐");

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == height - 1 && x == width - 1) {
                    System.out.print("   ");
                    continue;
                }
                System.out.print(grill[y][x].getWest() ? "│" : " ");

                System.out.print("   ");
            }

            if (y == height - 1) {
                System.out.println("   ");
                break;
            } else {
                System.out.println("│");
            }

            for (int x = 0; x < width; x++) {
                if (y == height - 1) {
                    System.out.print(grill[y][x].getWest() ? "│" : " ");
                    System.out.print("   ");
                    continue;
                }
                System.out.print(grill[y][x].getSouth() ? "├───" : "│   ");
            }

            System.out.println("│");
        }

        for (int x = 0; x < width; x++) {
            System.out.print("└───");
        }
        System.out.println("┘");
    }

    public static void printTerminal(Maze maze, List<Case> solutionPath) {
        int width = maze.getWidth();
        int height = maze.getHeight();
        Case[][] grill = maze.getMaze();
        boolean[][] isOnPath = new boolean[height][width];

        for (Case c : solutionPath) {
            isOnPath[c.getX()][c.getY()] = true;
        }

        for (int x = 0; x < width; x++) {
            System.out.print("┌───");
        }
        System.out.println("┐");

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == height - 1 && x == width - 1) {
                    System.out.print("   ");
                    continue;
                }

                System.out.print(grill[y][x].getWest() ? "│" : " ");
                System.out.print(isOnPath[y][x] ? "\u001B[31m • \u001B[0m" : "   ");
            }

            if (y == height - 1) {
                System.out.println("   ");
                break;
            } else {
                System.out.println("│");
            }

            for (int x = 0; x < width; x++) {
                System.out.print(grill[y][x].getSouth() ? "├───" : "│   ");
            }
            System.out.println("│");
        }

        for (int x = 0; x < width; x++) {
            System.out.print("└───");
        }
        System.out.println("┘");
    }

    public static void saveMazeTerminal(Maze currentMaze, Database db, Scanner sc) {
        List<String> existingNames = db.getMazeList();
        String name;

        while (true) {
            System.out.print("Entrez un nom unique pour sauvegarder le labyrinthe : ");
            name = sc.nextLine().trim();

            if (name.isEmpty()) {
                // Générer un nom par défaut unique
                int index = 1;
                name = "Labyrinthe";
                while (existingNames.contains(name)) {
                    name = "Labyrinthe_" + index++;
                }
                System.out.println("Aucun nom fourni. Le nom généré '" + name + "' sera utilisé.");
                break;
            } else if (existingNames.contains(name)) {
                System.out.println("Ce nom est déjà utilisé. Veuillez en choisir un autre.");
            } else {
                break; // nom unique et non vide
            }
        }

        db.SaveMaze(currentMaze, name);
        System.out.println("Labyrinthe sauvegardé sous le nom : " + name);
    }


    @FXML
    public void ResetEvent(ActionEvent event) {

    }

    @FXML
    public void SaveMaze() {
        String Name;
        try {
            Name = MazeName.getText();
        } catch (NumberFormatException e) {
            Name = "Labyrinthe";
        }
        db.SaveMaze(currentMaze, Name);
    }

    public void ChargeMaze(String name) {
        currentMaze = db.DataChargeMaze(name);
        gridPane.getChildren().clear();
        double cellWidth = mainPane.getWidth() / currentMaze.getWidth();
        double cellHeight = mainPane.getHeight() / currentMaze.getHeight();
        double cellSize = Math.min(cellWidth, cellHeight);

        gridPane.setPrefSize(currentMaze.getWidth() * cellSize, currentMaze.getHeight() * cellSize);

        for (int i = 0; i < currentMaze.getHeight(); i++) {
            for (int j = 0; j < currentMaze.getWidth(); j++) {
                Case cell = currentMaze.getMaze()[i][j];
                Pane pane = createCellPane(cell, cellSize);
                gridPane.add(pane, j, i);
            }
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setDatabase(Database db) {
        this.db = db;
    }


    private void setControlsDisabled(boolean disabled) {
        widthInput.setDisable(disabled);
        heightInput.setDisable(disabled);
        seedInput.setDisable(disabled);
        MethodGeneration.getToggles().forEach(toggle -> ((RadioButton) toggle).setDisable(disabled));
        MethodSolve.getToggles().forEach(toggle -> ((RadioButton) toggle).setDisable(disabled));
    }

    @FXML
    public void ReturnHomepage(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Homepage.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }
}






