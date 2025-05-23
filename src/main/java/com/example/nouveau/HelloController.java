package com.example.nouveau;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;



import javafx.scene.image.Image;

import java.io.IOException;
import java.sql.*;
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

    private Case entryCell = null;
    private Case exitCell = null;
    private boolean selectingEntryExit = false;
    private int selectionStep = 0; // 0: inactive, 1: selection of the entry 2: selection of the exit
    private boolean isPerfect;

    @FXML private ScrollPane mainPane;
    @FXML private GridPane gridPane;
    @FXML private TextField widthInput;
    @FXML private TextField heightInput;
    @FXML private TextField MazeName;
    @FXML private TextField seedInput;
    @FXML private TextField SpeedInputGeneration;
    @FXML private ToggleGroup MethodGeneration;
    @FXML private ToggleGroup MethodSolve;
    @FXML private ToggleButton toggleSwitch;
    @FXML private StackPane MazeStackPane;
    @FXML private ToggleButton toggleSwitchResolve;
    @FXML private TextField SpeedInputResolve;
    @FXML private Button SaveButton;
    @FXML private Button editModeButton;
    @FXML private Label mazeSizeLabel;
    @FXML private Label mazeSeedLabel;
    @FXML private Label mazePerfectLabel;
    @FXML private Label time;
    @FXML private Label NbCaseExplore;
    @FXML private Label NbCaseFinal;
    @FXML private ChoiceBox<String> SaveList;
    @FXML private Button selectEntryExitButton;


    /**
     * Initialise le contrôleur après le chargement du fichier FXML.
     * <p>
     * Cette méthode configure le mode plein écran pour la scène principale,
     * et initialise la visibilité ainsi que l'état des contrôles liés à la vitesse
     * et aux boutons d'édition et de sauvegarde.
     * </p>
     * <ul>
     *   <li>Si la scène est déjà disponible, elle applique directement le plein écran.</li>
     *   <li>Sinon, elle ajoute un écouteur pour appliquer le plein écran dès que la scène est disponible.</li>
     *   <li>Cache et désactive les contrôles liés à la vitesse de génération et de résolution.</li>
     *   <li>Désactive les boutons "mode édition" et "sauvegarder".</li>
     * </ul>
     */
    @FXML
    public void initialize() {
        Scene scene = mainPane.getScene();
        if (scene != null) {
            FullScreen(scene);
        } else {
            mainPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    FullScreen(newScene);
                }
            });
        }
        SpeedInputGeneration.setVisible(false);
        SpeedInputGeneration.setManaged(false);
        SpeedInputResolve.setVisible(false);
        SpeedInputResolve.setManaged(false);
        editModeButton.setDisable(true);
    }

    private void setupWithDatabase() {
        SaveList.setItems(db.getMazeList());
    }

    /**
     * Gère l'état du bouton bascule (toggleSwitch).
     * <p>
     * Lorsqu'il est activé (sélectionné), le texte du bouton change en "Désactiver" et
     * le champ SpeedInputGeneration devient visible et géré dans la mise en page.
     * <p>
     * Lorsqu'il est désactivé (non sélectionné), le texte revient à "Activer",
     * le champ SpeedInputGeneration est caché et non géré, et son contenu est vidé.
     */
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

    /**
     * Gère l'état du bouton bascule (toggleSwitch).
     * <p>
     *  Gère l'inverse de la fonction {@code handleToggle}.
     */
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

    /**
     * Génère un nouveau labyrinthe en fonction des paramètres saisis par l'utilisateur.
     * <p>
     * Le type de labyrinthe (parfait ou imparfait) est déterminé par la sélection radio.
     * Les dimensions du labyrinthe, ainsi que la seed aléatoire, sont saisies par l'utilisateur.
     * Des vérifications sont effectuées pour s'assurer de la validité des entrées.
     * <p>
     * En cas d'erreur (dimensions invalides ou vitesse hors limites), un message est affiché.
     * <p>
     * Une fois le labyrinthe généré, les cellules sont affichées dans une `GridPane`.
     * Si l'animation est activée, une `ProgressBar` suit l'avancement de la génération.
     * À la fin de la génération, l'entrée et la sortie du labyrinthe sont initialisées.
     * <p>
     * Désactive les boutons d'édition et de sauvegarde pendant la génération.
     */
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
        // ModeEdition.setDisable(true);
        editModeButton.setDisable(true);

        zoomFactor = 1.0;
        applyZoom();
        gridPane.getChildren().clear();
        int width, height, seed;
        try {
            width = Integer.parseInt(widthInput.getText());
            // Limites de génération 100 * 100
            if (width < 1 || width > 100) {
                showError("Taille invalide", "La taille doit être entre 1x1 et 100x100");
                return;
            }
        } catch (NumberFormatException e) {
            width = 20;
        }
        try {
            height = Integer.parseInt(heightInput.getText());
            // Limites de génération 100 * 100
            if (height < 1 || height > 100) {
                showError("Taille invalide", "La taille doit être entre 1x1 et 100x100");
                return;
            }
        } catch (NumberFormatException e) {
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
            if (speed < 10 || speed > 101) {
                showError("Vitesse invalide", "La vitesse doit être entre 10 et 100");
                return;
            }
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
                //ModeEdition.setDisable(false);
                editModeButton.setDisable(false);
                initializeEntryExit();


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
            //ModeEdition.setDisable(false);
            editModeButton.setDisable(false);
            initializeEntryExit();


        }
        isPerfect = currentMaze.isPerfect();

        mazeSizeLabel.setText("Taille : " + height + " x " + width);
        mazeSeedLabel.setText("Seed : " + seed);
        mazePerfectLabel.setText("Parfait : " + (isPerfect ? "Oui" : "Non"));
        time.setText("Temps de résolution :");
        NbCaseExplore.setText("Nombre de cases parcourues :");
        NbCaseFinal.setText("Nombre de cases du chemin final :");
    }

    /**
     * Réinitialise l'état actuel du labyrinthe et de l'interface graphique.
     * <p>
     *  Cette méthode est utilisée pour annuler une génération ou une résolution en cours
     *  et nettoyer l'interface avant de lancer une nouvelle opération.
     *  <p>
     *  Cette méthode laisse l'interface prête à accueillir une nouvelle génération.
     */
    @FXML
    public void resetMaze() {
        // on stoppe l'animation du serpent
        if (pathTimeline != null) {
            pathTimeline.stop();
            pathTimeline = null;
        }

        // stop la génération (surtout pour le pas à pas)
        if (generationTimeline != null) {
            generationTimeline.stop();
            generationTimeline = null;
            currentMaze = null;
            gridPane.getChildren().clear();
        }

        // supprime la bar de chargement
        if (progressBar != null) {
            MazeStackPane.getChildren().remove(progressBar);
            progressBar = null;
        }

        resetEntryExit();
        initializeEntryExit();
        selectingEntryExit = false;
        selectionStep = 0;
        selectEntryExitButton.setText("Choisir Entrée/Sortie");


        redrawMaze();
    }

    /** Permet de remettre le zoom à 0 */
    @FXML
    public void resetZoom() {
        zoomFactor = 1.0;
        applyZoom();
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
            
            mazeSizeLabel.setText("Taille : " + currentMaze.getHeight() + " x " + currentMaze.getWidth());
            mazeSeedLabel.setText("Seed : Labyrinthe personnalisé");
            mazePerfectLabel.setText("Parfait : " + (currentMaze.isPerfect() ? "Oui" : "Non"));
            time.setText("Temps de résolution : -");
            NbCaseExplore.setText("Nombre de cases parcourues : -");
            NbCaseFinal.setText("Nombre de cases du chemin final : -");
        }
    }


    /**
     * Active ou désactive le mode édition du labyrinthe.
     * <p>
     * En mode édition, l'utilisateur peut être modifer manuellement les cases du labyrinthe.
     * Cette méthode met à jour l'état du bouton d'édition, désactive certains contrôles
     * de l'interface utilisateur, et interrompt toute animation de résolution en cours.
     * <p>
     * Si le mode édition est désactivé, les contrôles sont réactivés et une validation du
     * labyrinthe est affectuée via {@code validateMaze()}.
     */
    @FXML
    private void toggleEntryExitSelection() {
        if (selectingEntryExit) {
            // Annuler sélection
            selectingEntryExit = false;
            selectionStep = 0;
            selectEntryExitButton.setText("Choisir Entrée/Sortie");
            setAllControlsDisabled(false); // Réactive tous les contrôles
        } else {
            selectingEntryExit = true;
            selectionStep = 1;
            selectEntryExitButton.setText("Sélectionnez l'ENTRÉE (bord)");

            // Stop animations
            if (pathTimeline != null) pathTimeline.stop();
            if (generationTimeline != null) generationTimeline.stop();

            // Désactiver controles
            setControlsDisabled(true);

            // Réinitialiser anciennes entrées/sorties
            resetEntryExit();
        }
        redrawMaze();
    }


    /**
     * Réinitialise les cellules d'entrée et de sortie du labyrinthe.
     * <p>
     * Cette méthode annule les sélections actuelles d'entrée et de sortie, restaure
     * leurs murs d'origine.
     * <p>
     */
    private void resetEntryExit() {
        // Sauvegarde des anciennes cellules
        Case oldEntry = entryCell;
        Case oldExit = exitCell;

        // Réinitialisation des références
        entryCell = null;
        exitCell = null;

        // Restauration des murs et rafraîchissement visuel
        if (oldEntry != null) {
            restoreBorderWall(oldEntry);
            refreshCell(oldEntry);
        }

        if (oldExit != null) {
            restoreBorderWall(oldExit);
            refreshCell(oldExit);
        }
    }

    /**
     * Rafraîchit l'affichage visuel d'une cellule spécifique dans la grille du labyrinthe.
     * <p>
     * Cette méthode supprime l'ancien panneau correspondant à la cellule dans le `gridPane`,
     * puis en recrée un nouveau avec les propriétés actuelles de la cellule (murs, couleurs, etc.).
     *
     * @param cell La cellule {@link Case} à mettre à jour visuellement.
     */
    private void refreshCell(Case cell) {
        Pane pane = getPaneFromGrid(cell.getY(), cell.getX());
        if (pane != null) {
            double cellSize = pane.getPrefWidth();
            Pane newPane = createCellPane(cell, cellSize);
            gridPane.getChildren().remove(pane);
            gridPane.add(newPane, cell.getY(), cell.getX());
        }
    }


    /**
     * Gère la sélection d'une cellule pour définir l'entrée ou la sortie du labyrinthe.
     * <p>
     * Cette méthode est utilisée lors du mode de sélection d'entrée/sortie activé par l'utilisateur.
     * Elle vérifie que la cellule sélectionnée se trouve sur les bords du labyrinthe, et distingue
     * si la cellule doit devenir l'entrée ou la sortie en fonction de l'état de progression.
     *
     * @param cell La cellule sélectionnée par l'utilisateur.
     */
    private void handleCellSelectionForEntryExit(Case cell) {
        if (!selectingEntryExit) return;

        if (!isBorderCell(cell)) {
            showError("Sélection invalide", "L'entrée et la sortie doivent être sur les bords du labyrinthe.");
            return;
        }

        if (selectionStep == 1) {
            // Réinitialise l'ancienne entrée si elle existe
            if (entryCell != null) {
                restoreBorderWall(entryCell);
                refreshCell(entryCell);
            }

            entryCell = cell;
            removeBorderWall(entryCell);
            refreshCell(entryCell);

            selectionStep = 2;
            selectEntryExitButton.setText("Sélectionnez la SORTIE (bord)");
        }
        else if (selectionStep == 2) {
            if (cell == entryCell) {
                showError("Sélection invalide", "La sortie doit être différente de l'entrée.");
                return;
            }

            // Réinitialise l'ancienne sortie si elle existe
            if (exitCell != null) {
                restoreBorderWall(exitCell);
                refreshCell(exitCell);
            }

            exitCell = cell;
            removeBorderWall(exitCell);
            refreshCell(exitCell);

            selectingEntryExit = false;
            selectionStep = 0;
            selectEntryExitButton.setText("Choisir Entrée/Sortie");
            setControlsDisabled(false);
        }
    }

    /**
     * Vérifie si une cellule se trouve sur la bordure du labyrinthe.
     * <p>
     * Une cellule est considérée comme une cellule de bordure si elle est située
     * sur la première ou la dernière ligne, ou sur la première ou la dernière colonne
     * de la grille du labyrinthe.
     *
     * @param cell La cellule à vérifier.
     * @return {@code true} si la cellule est sur un bord du labyrinthe, sinon {@code false}.
     */
    private boolean isBorderCell(Case cell) {
        return cell.getX() == 0 || cell.getX() == currentMaze.getHeight()-1 ||
                cell.getY() == 0 || cell.getY() == currentMaze.getWidth()-1;
    }


    /**
     * Rétablit le mur sur le bord correspondant de la cellule donnée.
     *
     * @param cell La cellule dont le mur de bordure doit être restauré.
     */
    private void restoreBorderWall(Case cell) {
        if (cell.getX() == 0) cell.setNorth(true);
        else if (cell.getX() == currentMaze.getHeight()-1) cell.setSouth(true);
        else if (cell.getY() == 0) cell.setWest(true);
        else if (cell.getY() == currentMaze.getWidth()-1) cell.setEast(true);
    }

    /**
     * Supprime le mur sur le bord correspondant de la cellule donnée.
     *
     * @param cell La cellule dont le mur de bordure doit être supprimé.
     */
    private void removeBorderWall(Case cell) {
        if (cell.getX() == 0) cell.setNorth(false);
        else if (cell.getX() == currentMaze.getHeight()-1) cell.setSouth(false);
        else if (cell.getY() == 0) cell.setWest(false);
        else if (cell.getY() == currentMaze.getWidth()-1) cell.setEast(false);
    }


    /**
     * Initialise les cellules d'entrée et de sortie du labyrinthe..
     */
    private void initializeEntryExit() {
        if (currentMaze == null) return;

        // Entrée par défaut
        entryCell = currentMaze.getMaze()[0][0];
        entryCell.setNorth(false);

        // Sortie par défaut
        exitCell = currentMaze.getMaze()[currentMaze.getHeight() - 1][currentMaze.getWidth() - 1];
        exitCell.setSouth(false);

        redrawMaze();
    }


    /**
     * Valide l'état actuel du labyrinthe après la fin du mode édition.
     */
    private void validateMaze() {
        // Par exemple, on pourrait sauvegarder automatiquement le labyrinthe ou juste afficher un message
        System.out.println("Labyrinthe validé !");
        // ou tu peux appeler ta méthode de sauvegarde, ou juste redessiner sans édition possible
        redrawMaze();
    }


    /**
     * Crée un panneau graphique représentant une cellule du labyrinthe.
     * <p>
     * Cette méthode construit visuellement la cellule en fonction de ses murs.
     * <p>
     * Elle gère aussi l'interaction utilisateur lors d'un clic :
     * <ul>
     *   <li>Si le mode sélection d'entrée/sortie est activé, elle délègue la gestion
     *       de la sélection à {@code handleCellSelectionForEntryExit}.</li>
     *   <li>Si le mode édition est activé, elle permet de modifier les murs intérieurs
     *       en cliquant sur les bords de la cellule, sauf les murs extérieurs qui sont
     *       protégés.</li>
     * </ul>
     *
     * @param cell la cellule du labyrinthe à représenter graphiquement
     * @param cellSize la taille (largeur et hauteur) en pixels du panneau représentant la cellule
     * @return un objet {@code Pane} configuré graphiquement et avec les événements associés
     */
    private Pane createCellPane(Case cell, double cellSize) {
        Pane pane = new Pane();
        pane.setPrefSize(cellSize, cellSize);

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


        // Mur en trait
        String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: " +
                (cell.getNorth() ? "1 " : "0 ") +
                (cell.getEast() ? "1 " : "0 ") +
                (cell.getSouth() ? "1 " : "0 ") +
                (cell.getWest() ? "1" : "0") + ";";


        // Entrée/sortie
        if (cell == entryCell) {
            style += " -fx-background-color: #00ff00;"; // Vert vif pour l'entrée
            Label label = new Label("E");
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
            label.setLayoutX(cellSize/2 - 5);
            label.setLayoutY(cellSize/2 - 10);
            pane.getChildren().add(label);
        } else if (cell == exitCell) {
            style += " -fx-background-color: #ff0000;"; // Rouge vif pour la sortie
            Label label = new Label("S");
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
            label.setLayoutX(cellSize/2 - 5);
            label.setLayoutY(cellSize/2 - 10);
            pane.getChildren().add(label);
        } else {
            style += " -fx-background-color: white;";
        }


        pane.setStyle(style);


        pane.setOnMouseClicked(event -> {
            if (selectingEntryExit) {
                handleCellSelectionForEntryExit(cell);
            } else if (editMode) {
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
            }
            redrawMaze();
        });

        return pane;
    }


    /**
     * Redessine entièrement le labyrinthe sur la grille graphique.
     * <p>
     * Cette méthode parcourt chaque cellule du labyrinthe {@code currentMaze} et
     * met à jour le panneau graphique correspondant dans {@code gridPane} pour
     * refléter l'état actuel des murs et la position des cellules d'entrée et de sortie.
     * <p>
     * Les murs peuvent être affichés sous forme de bordures CSS noires, et la
     * cellule d'entrée est colorée en vert, tandis que la sortie est en rouge.
     * <p>
     * Cette méthode permet ainsi de synchroniser l'affichage avec les données
     * internes du labyrinthe après des modifications, comme en mode édition ou
     * après une génération.
     *
     * @throws NullPointerException si {@code currentMaze} est null (évité par un test au début)
     */
    private void redrawMaze() {
        if (currentMaze == null) return;

        for (int i = 0; i < currentMaze.getHeight(); i++) {
            for (int j = 0; j < currentMaze.getWidth(); j++) {
                Case cell = currentMaze.getMaze()[i][j];
                Pane pane = (Pane) getNodeFromGridPane(gridPane, j, i);

                if (pane != null) {
                    

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
                    pane.setStyle(null); // Réinitialise le style avant de le remettre
                    pane.setStyle("-fx-background-color: " +
                            (cell == entryCell ? "#00ff00" :
                                    cell == exitCell ? "#ff0000" : "white") +
                            "; -fx-border-color: black; -fx-border-width: " +
                            (cell.getNorth() ? "1 " : "0 ") +
                            (cell.getEast() ? "1 " : "0 ") +
                            (cell.getSouth() ? "1 " : "0 ") +
                            (cell.getWest() ? "1" : "0") + ";");
                }
            }
        }
    }


    /**
     * Retourne le noeud graphique présent dans une cellule spécifique d'un GridPane.
     * <p>
     * Cette méthode parcourt les enfants du {@code gridPane} pour trouver un noeud
     * situé à la colonne {@code col} et la ligne {@code row} spécifiées.
     *
     * @param gridPane Le GridPane dans lequel chercher.
     * @param col La colonne recherchée.
     * @param row La ligne recherchée.
     * @return Le Node à la position ({@code col}, {@code row}) ou {@code null} s'il n'existe pas.
     */
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


    /**
     * Gère l'événement de zoom sur la grille du labyrinthe via la molette de la souris.
     * <p>
     * Augmente ou diminue le facteur de zoom selon la direction de la molette.
     * Un zoom positif (vers l'avant) augmente le facteur de zoom de 10%,
     * un zoom négatif (vers l'arrière) le réduit de 10%.
     * <p>
     * Appelle ensuite la méthode {@code applyZoom()} pour appliquer l'effet visuel.
     *
     * @param event L'événement de défilement de la molette de souris déclenchant le zoom.
     */
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


    /**
     * Change le curseur de la souris en une main fermée lorsque la souris est pressée.
     * <p>
     * Utilisé généralement pour indiquer que l'utilisateur peut glisser ou déplacer une zone.
     */
    @FXML
    public void MousePressed() {
        mainPane.setCursor(Cursor.CLOSED_HAND);
    }


    /**
     * Lance la résolution du labyrinthe sélectionné selon la méthode choisie.
     * <p>
     * Cette méthode :
     * <ul>
     *   <li> vérifie que le mode édition est désactivé (sinon elle bloque la résolution),</li>
     *   <li> arrête toute animation en cours (chemin ou génération),</li>
     *   <li> nettoie la barre de progression, </li>
     *   <li> redessine le labyrinthe, </li>
     *   <li> instancie un objet Resolve pour résoudre le labyrinthe, </li>
     *   <li> récupère la méthode de résolution choisie (Tremaux, BFS, Hand on Wall), </li>
     *   <li> exécute la méthode correspondante, </li>
     *   <li> affiche une erreur si aucun chemin n’a été trouvé, </li>
     *   <li> affiche le temps, le nombre de cases explorées et la longueur du chemin final, </li>
     *   <li> enfin affiche le chemin soit d’un coup, soit étape par étape selon le toggle. </li>
     * </ul>
     */
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
        Resolve solver = new Resolve(currentMaze, entryCell, exitCell);

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

        long duration = solver.getDuration();
        time.setText("Temps de résolution : " + duration + " ms");
        NbCaseExplore.setText("Nombre de cases parcourues : " + solver.getNbCase());
        NbCaseFinal.setText("Nombre de cases du chemin final : " + path.size());

        if (toggleSwitchResolve.isSelected()) {
            showPathStepByStep(path);
        } else {
            drawPath(path);
        }
    }


    /**
     * Colore en orange (#ff6a00) le fond des cellules représentant le chemin donné dans la grille.
     * Chaque cellule du chemin est mise en évidence visuellement.
     *
     * @param path Liste ordonnée des cellules (Cases) formant le chemin à dessiner.
     *             Si la liste est nulle ou vide, la méthode ne fait rien.
     */
    private void drawPath(List<Case> path) {
        if (path == null || path.isEmpty()) return;

        for (Case c : path) {
            Pane cellPane = getPaneFromGrid(c.getY(), c.getX()); // Attention : x = ligne, y = colonne
            if (cellPane != null) {
                cellPane.setStyle(cellPane.getStyle() + "; -fx-background-color: #ff6a00");
            }
        }
    }


    /**
     * Recherche et retourne le noeud (Pane) contenu dans la GridPane à la position spécifiée.
     *
     * @param col numéro de colonne (axe horizontal) dans la grille.
     * @param row numéro de ligne (axe vertical) dans la grille.
     * @return Le Node correspondant à cette position dans la GridPane, ou null si aucun nœud n'est trouvé.
     */
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


    /**
     * Affiche le chemin donné dans la grille en le colorant étape par étape avec une animation.
     * Chaque cellule du chemin est mise en surbrillance en rouge selon la vitesse spécifiée.
     * <p>
     * Si le mode édition est activé, l'animation en cours est arrêtée et réinitialisée.
     * La vitesse d'animation est récupérée depuis l'entrée utilisateur, avec une valeur par défaut de 100 ms.
     * La vitesse doit être comprise entre 10 et 100 ms, sinon un message d'erreur est affiché.
     *
     * @param path Liste ordonnée des cellules (Cases) formant le chemin à animer.
     *             Si la liste est vide ou nulle, la méthode n'affiche rien.
     */
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
        if (speed < 10 || speed > 101) {
            showError("Vitesse invalide", "La vitesse doit être entre 10 et 100");
            return;
        }
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


    /**
     * Génère un labyrinthe selon la méthode spécifiée, avec une taille donnée et un seed optionnel.
     *
     * @param width   La largeur (nombre de colonnes) du labyrinthe à générer.
     * @param height  La hauteur (nombre de lignes) du labyrinthe à générer.
     * @param method  La méthode de génération du labyrinthe :
     *                "Parfait" pour un labyrinthe parfait (sans cycles),
     *                toute autre valeur pour un labyrinthe imparfait.
     * @param seedOpt Un entier optionnel utilisé comme seed pour la génération aléatoire.
     *                S'il est null, un seed aléatoire sera généré.
     * @return        Un objet Maze représentant le labyrinthe généré.
     */
    public static Maze generateMaze(int width, int height, String method, Integer seedOpt) {
        int seed = (seedOpt != null) ? seedOpt : new Random().nextInt();
        Maze maze = new Maze(width, height);
        LinkedList<int[]> steps;
        if ("Parfait".equalsIgnoreCase(method)) {
            steps = maze.KruskalGeneration(seed);
        } else {
            steps = maze.KruskalImperfectGeneration(seed);
        }
        while (!steps.isEmpty()) {
            int[] wall = steps.poll();
            if ("Parfait".equalsIgnoreCase(method)) {
                maze.setWallsPerfect(wall, maze.getMaze()[wall[0]][wall[1]], maze.getMaze()[wall[2]][wall[3]]);
            } else {
                maze.setWallsPerfectImperfect(wall, maze.getMaze()[wall[0]][wall[1]], maze.getMaze()[wall[2]][wall[3]]);
            }
        }
        return maze;
    }


    /**
     * Affiche une représentation textuelle du labyrinthe dans le terminal.
     *
     * Cette méthode utilise des caractères Unicode pour dessiner les murs du labyrinthe
     * selon les informations contenues dans l'objet {@code Maze} passé en paramètre.
     *
     * La bordure supérieure et inférieure ainsi que les jonctions entre les murs sont également dessinées
     * pour former un cadre cohérent autour du labyrinthe.
     *
     * @param maze L'objet {@code Maze} représentant le labyrinthe à afficher.
     *             Il doit fournir la largeur, la hauteur et la grille des cases via
     *             les méthodes {@code getWidth()}, {@code getHeight()} et {@code getMaze()}.
     */
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


    /**
     * Affiche une représentation textuelle du labyrinthe dans le terminal,
     * en mettant en évidence un chemin solution.
     * <p>
     * Cette méthode dessine le labyrinthe avec des caractères Unicode pour représenter
     * les murs, et affiche le chemin solution fourni en rouge sous forme de points "•".
     * <p>
     * Le labyrinthe est représenté case par case, avec des murs à l'ouest et au sud selon
     * les informations fournies par les méthodes {@code getWest()} et {@code getSouth()} de chaque case.
     * <p>
     * Le chemin solution est indiqué par des points rouges dans les cases correspondantes.
     *
     * @param maze Le labyrinthe à afficher, contenant la largeur, la hauteur et la grille des cases.
     * @param solutionPath La liste ordonnée des cases formant le chemin solution à mettre en évidence.
     */
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



    /**
     * Sauvegarde un labyrinthe dans la base de données avec un nom unique fourni par l'utilisateur.
     * <p>
     * Cette méthode demande à l'utilisateur, via la console, de saisir un nom unique pour enregistrer
     * le labyrinthe. Si l'utilisateur ne fournit pas de nom (entrée vide), un nom par défaut est généré
     * automatiquement en s'assurant qu'il soit unique parmi les noms existants dans la base.
     * <p>
     * Si le nom saisi existe déjà, l'utilisateur est invité à en saisir un autre jusqu'à obtenir un nom unique.
     *
     * @param currentMaze Le labyrinthe à sauvegarder.
     * @param db L'objet Database utilisé pour la gestion des sauvegardes.
     * @param sc Le Scanner utilisé pour lire l'entrée utilisateur depuis la console.
     */
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


    /**
     * Sauvegarde le labyrinthe courant dans la base de données avec le nom spécifié par l'utilisateur.
     * <p>
     * Cette méthode vérifie que le champ de texte contenant le nom du labyrinthe n'est pas vide,
     * et que ce nom n'existe pas déjà dans la liste des labyrinthes enregistrés.
     * Si une de ces conditions n'est pas respectée, un message d'erreur est affiché.
     * Sinon, le labyrinthe est sauvegardé dans la base via l'objet `db`.
     * <p>
     * La méthode dépend d'un champ texte `MazeName` pour récupérer le nom du labyrinthe,
     * et d'une instance `currentMaze` représentant le labyrinthe courant.
     */
    @FXML
    public void SaveMaze() {
        ObservableList<String> ListMaze = db.getMazeList();
        if(MazeName.getText().isEmpty()){
            showError("Erreur de Sauvegarde", "Veuillez entrer un nom pour le labyrinthe.");
        }
        else if(ListMaze.contains(MazeName.getText())){
            showError("Erreur de Sauvegarde", "Ce labyrinthe existe déjà, veuillez entrer un nouveau nom");
        }
        else{
            db.SaveMaze(currentMaze, MazeName.getText());
            SaveList.setItems(db.getMazeList());
            showMessage("Sauvegarde", "Labyrinthe Sauvegardé avec succès !");
        }
    }

    @FXML
    public void loadMaze() {
        if(!SaveList.getItems().isEmpty() && SaveList.getValue() != null){
            ChargeMaze(SaveList.getValue());
        }
        else{
            showError("Erreur de restauration", "Aucun labyrinthe à charger");
        }
    }

    @FXML
    public void deleteMaze() {
        db.DeleteMaze(SaveList.getValue());
        SaveList.setItems(db.getMazeList());
    }

    /**
     * Charge un labyrinthe depuis la base de données et met à jour l'affichage.
     * Calcule la taille des cellules, initialise la grille et détecte l'entrée et la sortie.
     *
     * @param name Le nom du labyrinthe à charger.
     */
    public void ChargeMaze(String name) {
        currentMaze = db.DataChargeMaze(name);
        gridPane.getChildren().clear();
        double cellWidth = mainPane.getWidth() / currentMaze.getWidth();
        double cellHeight = mainPane.getHeight() / currentMaze.getHeight();
        double cellSize = Math.min(cellWidth, cellHeight);

        gridPane.setPrefSize(currentMaze.getWidth() * cellSize, currentMaze.getHeight() * cellSize);

        // Trouver l'entrée et la sortie dans le labyrinthe chargé
        entryCell = null;
        exitCell = null;
        for (int i = 0; i < currentMaze.getHeight(); i++) {
            for (int j = 0; j < currentMaze.getWidth(); j++) {
                Case cell = currentMaze.getMaze()[i][j];
                if ((i == 0 && !cell.getNorth()) || (i == currentMaze.getHeight() - 1 && !cell.getSouth()) || (j == 0 && !cell.getWest()) || (j == currentMaze.getWidth() - 1 && !cell.getEast())) {
                    if (entryCell == null) {
                        entryCell = cell;
                    } else if (exitCell == null) {
                        exitCell = cell;
                    }
                }

                Pane pane = createCellPane(cell, cellSize);
                gridPane.add(pane, j, i);
            }
        }
    }


    /**
     * Affiche une boîte de dialogue d'erreur avec un titre et un message.
     *
     * @param title   Le titre de la fenêtre d'erreur.
     * @param message Le message d'erreur à afficher.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Définit la base de données utilisée par le contrôleur.
     *
     * @param db L'objet Database à utiliser.
     */
    public void setDatabase(Database db) {
        this.db = db;
        setupWithDatabase();
    }


    /**
     * Active ou désactive les contrôles de l'interface utilisateur.
     *
     * @param disabled true pour désactiver les contrôles, false pour les activer.
     */
    private void setControlsDisabled(boolean disabled) {
        widthInput.setDisable(disabled);
        heightInput.setDisable(disabled);
        seedInput.setDisable(disabled);
        MethodGeneration.getToggles().forEach(toggle -> ((RadioButton) toggle).setDisable(disabled));
        MethodSolve.getToggles().forEach(toggle -> ((RadioButton) toggle).setDisable(disabled));
        selectEntryExitButton.setDisable(disabled && !selectingEntryExit); // Permet d'annuler la sélection
    }


    /**
     * Active ou désactive tous les contrôles de l'interface, s'ils sont initialisés.
     *
     * @param disabled true pour désactiver les contrôles, false pour les activer.
     */
    private void setAllControlsDisabled(boolean disabled) {
        // Désactive tous les contrôles
        if (widthInput != null) widthInput.setDisable(disabled);
        if (heightInput != null) heightInput.setDisable(disabled);
        if (seedInput != null) seedInput.setDisable(disabled);
        if (SpeedInputGeneration != null) SpeedInputGeneration.setDisable(disabled);
        //if (SpeedInputResolve != null) SpeedInputResolve.setDisable(disabled);
        if (MazeName != null) MazeName.setDisable(disabled);
        if (SaveButton != null) SaveButton.setDisable(disabled);
        if (editModeButton != null) editModeButton.setDisable(disabled);
        if (toggleSwitch != null) toggleSwitch.setDisable(disabled);
        //if (toggleSwitchResolve != null) toggleSwitchResolve.setDisable(disabled);

        // RadioButtons
        if (MethodGeneration != null) {
            MethodGeneration.getToggles().forEach(toggle -> ((RadioButton) toggle).setDisable(disabled));
        }
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

    private void FullScreen(Scene scene){
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                Stage stage = (Stage) scene.getWindow();
                stage.setFullScreen(!stage.isFullScreen());
                if(currentMaze != null){
                    double cellWidth = mainPane.getWidth() / currentMaze.getWidth();
                    double cellHeight = mainPane.getHeight() / currentMaze.getHeight();
                    double cellSize = Math.min(cellWidth, cellHeight);
                    gridPane.setPrefSize(currentMaze.getWidth() * cellSize, currentMaze.getHeight() * cellSize);
                    redrawMaze();
                }
            }
        });
    }

}




