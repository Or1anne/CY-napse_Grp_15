package com.example.nouveau;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.sql.*;


/**
 * Contrôleur de la page d'accueil de l'application.
 *
 * <p>Gère l'interface, les animations d'ouverture, le chargement des sauvegardes de labyrinthes
 * et la navigation vers la génération de labyrinthes.</p>
 *
 * <p>Responsabilités principales :</p>
 * <ul>
 *   <li>Initialisation de la base de données</li>
 *   <li>Gestion de la taille et position dynamique des éléments UI</li>
 *   <li>Animations pour le démarrage et le chargement</li>
 *   <li>Affichage et suppression des sauvegardes</li>
 *   <li>Basculement plein écran avec F11</li>
 * </ul>
 */
public class HomepageController {
    @FXML private AnchorPane Begin;
    @FXML private AnchorPane BorderSave;
    @FXML private ImageView BGHome;
    @FXML private Label StartButton;
    @FXML private Button ChargeSave;
    @FXML private Button NewLab;
    @FXML private Label CyNapse;
    @FXML private HBox HBoxSave;
    @FXML private Button GoBack;

    private HelloController controller;
    private Database db;


    /**
     * Initialise la page d'accueil en configurant la base de données,
     * gère le plein écran, et adapte dynamiquement la taille et la position
     * des éléments graphiques en fonction de la taille de la fenêtre.
     */
    @FXML
    public void initialize() {
        //Initialisation de la BDD
        db = new Database();
        db.createDatabase();
        db.createTable();

        Scene scene = Begin.getScene();
        if (scene != null) {
            FullScreen(scene);
        } else {
            Begin.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    FullScreen(newScene);
                }
            });
        }

        //Lie la taille de l'image avec celle de l'AnchorPane
        BGHome.setPreserveRatio(false);
        BGHome.fitWidthProperty().bind(Begin.widthProperty());
        BGHome.fitHeightProperty().bind(Begin.heightProperty());

        //Lie la taille de l'AnchorPane avec celle de son parent
        BorderSave.prefWidthProperty().bind(Begin.widthProperty().multiply(0.8));
        BorderSave.prefHeightProperty().bind(Begin.heightProperty().multiply(0.3));

        //Adapte la taille des éléments avec celle du AnchorPane
        Begin.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                ReplaceLabel(StartButton, 0.47, 0.6, 0.045);
                ReplaceLabel(CyNapse, 0.47, 0.2, 0.045);
                ReplaceButton(NewLab, 0.30, 0.65, 0.02);
                ReplaceButton(ChargeSave, 0.65, 0.65, 0.02);
                ReplaceAnchorPane(BorderSave, 0.5, 0.6);
                ReplaceButton(GoBack, 0.15, 0.42, 0.02);
            });
        });

        //Réadaptation (Je sais pas pourquoi mais il faut le faire)
        ActiveReplaceLabel(StartButton, 0.47, 0.6, 0.045);
        ActiveReplaceLabel(CyNapse, 0.47, 0.2, 0.045);
        ActiveReplaceButton(NewLab, 0.30, 0.65, 0.02);
        ActiveReplaceButton(ChargeSave, 0.65, 0.65, 0.02);
        ActiveReplaceAnchorPane(BorderSave, 0.5, 0.6);
        ActiveReplaceButton(GoBack, 0.15, 0.42, 0.02);
    }

    //Méthodes pour déplacer les éléments et adapte leur taille en fonction de la taille de la page

    /**
     * Ajuste la taille de la police d'un Label en fonction de la largeur de l'AnchorPane 'Begin',
     * puis positionne le Label de manière centrée sur la fenêtre selon des coordonnées relatives.
     *
     * @param label Le Label à redimensionner et repositionner.
     * @param x La position horizontale relative (entre 0.0 et 1.0) où centrer le Label.
     * @param y La position verticale relative (entre 0.0 et 1.0) où centrer le Label.
     * @param fontSizeRatio Le ratio appliqué à la largeur de 'Begin' pour définir la taille de la police.
     */
    private void ReplaceLabel(Label label, double x, double y, double fontSizeRatio) {
        label.setStyle("-fx-font-size: " + Begin.getWidth() * fontSizeRatio + "px;");
        Platform.runLater(() -> {
                double centerX = Begin.getWidth() * x;
                double centerY = Begin.getHeight() * y;

                label.setLayoutX(centerX - label.getWidth() / 2);
                label.setLayoutY(centerY - label.getHeight() / 2);
        });
    }


    /**
     * Active un ajustement dynamique de la taille et de la position d'un Label.
     * Cette méthode ajoute des écouteurs sur les propriétés de largeur et hauteur du Label,
     * afin de repositionner et redimensionner le Label automatiquement à chaque changement de taille.
     *
     * @param label Le Label à surveiller et repositionner.
     * @param x La position horizontale relative (entre 0.0 et 1.0) où centrer le Label.
     * @param y La position verticale relative (entre 0.0 et 1.0) où centrer le Label.
     * @param FontSize Le ratio appliqué à la largeur de 'Begin' pour définir la taille de la police.
     */
    private void ActiveReplaceLabel(Label label, double x, double y, double FontSize) {
        label.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceLabel(label, x, y, FontSize));
        });

        label.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceLabel(label, x, y, FontSize));
        });
    }


    /**
     * Redimensionne et repositionne un bouton dans la fenêtre en fonction d'un pourcentage relatif.
     * La taille de la police est ajustée proportionnellement à la largeur actuelle de la scène 'Begin'.
     * Le bouton est centré autour des coordonnées calculées par rapport à la taille de la scène.
     *
     * @param btn Le bouton à redimensionner et repositionner.
     * @param x La position horizontale relative (entre 0.0 et 1.0) pour centrer le bouton.
     * @param y La position verticale relative (entre 0.0 et 1.0) pour centrer le bouton.
     * @param fontSizeRatio Le ratio appliqué à la largeur de 'Begin' pour définir la taille de la police.
     */
    private void ReplaceButton(Button btn, double x, double y, double fontSizeRatio) {
        btn.setStyle("-fx-font-size: " + Begin.getWidth() * fontSizeRatio + "px;");

            Platform.runLater(() -> {
                double centerX = Begin.getWidth() * x;
                double centerY = Begin.getHeight() * y;

                btn.setLayoutX(centerX - btn.getWidth() / 2);
                btn.setLayoutY(centerY - btn.getHeight() / 2);
            });
    }


    /**
     * Active un écouteur sur les changements de taille (largeur et hauteur) du bouton.
     * À chaque modification, repositionne et redimensionne le bouton en appelant la méthode ReplaceButton.
     *
     * @param btn Le bouton sur lequel écouter les changements de taille.
     * @param x La position horizontale relative (entre 0.0 et 1.0) pour centrer le bouton.
     * @param y La position verticale relative (entre 0.0 et 1.0) pour centrer le bouton.
     * @param FontSize Le ratio appliqué à la largeur de 'Begin' pour définir la taille de la police.
     */
    private void ActiveReplaceButton(Button btn, double x, double y, double FontSize) {
        btn.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceButton(btn, x, y, FontSize));
        });

        btn.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceButton(btn, x, y, FontSize));
        });
    }


    /**
     * Positionne un AnchorPane de manière centrée en fonction de coordonnées relatives
     * exprimées par rapport à la taille de la scène 'Begin'.
     *
     * @param anchor L'AnchorPane à positionner.
     * @param x Position horizontale relative (entre 0.0 et 1.0) pour centrer l'AnchorPane.
     * @param y Position verticale relative (entre 0.0 et 1.0) pour centrer l'AnchorPane.
     */
    private void ReplaceAnchorPane(AnchorPane anchor, double x, double y) {
        double centerX = Begin.getWidth() * x;
        double centerY = Begin.getHeight() * y;

        anchor.setLayoutX(centerX - anchor.getWidth() / 2);
        anchor.setLayoutY(centerY - anchor.getHeight() / 2);
    }


    /**
     * Active le repositionnement automatique et centré d'un AnchorPane
     * en fonction des changements de sa taille.
     *
     * @param anchor L'AnchorPane à repositionner.
     * @param x Position horizontale relative (entre 0.0 et 1.0) pour centrer l'AnchorPane.
     * @param y Position verticale relative (entre 0.0 et 1.0) pour centrer l'AnchorPane.
     */
    private void ActiveReplaceAnchorPane(AnchorPane anchor, double x, double y) {
        anchor.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceAnchorPane(anchor, x, y));
        });

        anchor.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceAnchorPane(anchor, x, y));
        });
    }


    //Adapte la taille
    /**
     * Adapte la taille d'une ImageView pour qu'elle remplisse entièrement la scène donnée,
     * en liant sa largeur et sa hauteur à celles de la scène.
     * Le ratio d'aspect de l'image n'est pas conservé (image peut être déformée).
     *
     * @param view L'ImageView à redimensionner.
     * @param scene La scène dont la taille sera suivie.
     */
    private void AdaptSize(ImageView view, Scene scene) {
        view.fitWidthProperty().bind(scene.widthProperty());
        view.fitHeightProperty().bind(scene.heightProperty());
        view.setPreserveRatio(false);
    }


    /**
     * Lance une nouvelle partie en désactivant le clic sur 'Begin',
     * puis fait disparaître le bouton StartButton avec une transition de fondu,
     * et affiche ensuite les boutons de sélection des niveaux.
     */
    @FXML
    void NewGame() {
        Begin.setOnMouseClicked(null);
        FadeTransition finalFade = new FadeTransition(Duration.seconds(1), StartButton);
        finalFade.setFromValue(StartButton.getOpacity());
        finalFade.setToValue(0);
        finalFade.setOnFinished(e -> {
            StartButton.setVisible(false);
            showLevelButtons();
        });
        finalFade.play();
    }


    /**
     * Affiche les boutons "NewLab" et "ChargeSave" avec une animation de fondu.
     * <p>
     * Les boutons sont d'abord rendus visibles, puis leur opacité passe de 0 à 1
     * sur une durée d'une seconde, créant un effet d'apparition progressive.
     * </p>
     */
    private void showLevelButtons() {

        FadeTransition fade1 = new FadeTransition(Duration.seconds(1), NewLab);
        fade1.setFromValue(0);
        fade1.setToValue(1);

        FadeTransition fade2 = new FadeTransition(Duration.seconds(1), ChargeSave);
        fade2.setFromValue(0);
        fade2.setToValue(1);

        fade1.setOnFinished(e -> NewLab.setVisible(true));
        fade2.setOnFinished(e -> ChargeSave.setVisible(true));

        fade1.play();
        fade2.play();
    }


    /**
     * Ouvre la fenêtre de génération de labyrinthe.
     *
     * Cette méthode récupère la fenêtre (Stage) à partir de l'événement de souris
     * et appelle la méthode OpenMazeGeneration(Stage, Object) avec un second
     * argument nul.
     *
     * @param event l'événement de souris qui déclenche l'ouverture
     * @throws IOException si le chargement de la fenêtre échoue
     */
    @FXML
    public void OpenMazeGeneration(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        OpenMazeGeneration(stage, null);
    }



    /**
     * Ouvre la fenêtre de génération de labyrinthe avec une animation GIF.
     * <p>
     * Cette méthode affiche un GIF animé pendant 4 secondes avant de charger
     * la vue de génération du labyrinthe depuis un fichier FXML. Une fois la vue
     * chargée, elle remplace la scène actuelle par cette nouvelle scène.
     * <p>
     * Un effet visuel est appliqué au GIF pour modifier sa saturation et sa luminosité.
     * Si un Runnable `onFinished` est fourni, il sera exécuté une fois la transition terminée.
     *
     * @param stage la fenêtre (Stage) sur laquelle afficher la scène de génération
     * @param onFinished un Runnable optionnel à exécuter après le chargement de la scène (peut être null)
     * @throws IOException si le chargement du fichier FXML échoue
     */
    public void OpenMazeGeneration(Stage stage, Runnable onFinished) throws IOException {
        Image gif = new Image(getClass().getResource("StartNewMaze.gif").toExternalForm());
        ImageView gifView = new ImageView(gif);
        AdaptSize(gifView, stage.getScene());
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-0.5);
        colorAdjust.setBrightness(-0.2);
        gifView.setEffect(colorAdjust);

        Begin.getChildren().add(gifView);

        PauseTransition pause = new PauseTransition(Duration.seconds(4));
        pause.setOnFinished(ev -> {
            Begin.getChildren().remove(gifView);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            controller = fxmlLoader.getController();
            controller.setDatabase(this.db);

            Scene scene = new Scene(root, 1000, 600);
            stage.setScene(scene);
            stage.show();

            if (onFinished != null) {
                onFinished.run();
            }
        });
        pause.play();
    }



    /**
     * Affiche une animation de chargement avec un GIF, puis affiche l'écran de sauvegarde.
     * Pendant l'animation, les boutons NewLab et ChargeSave sont masqués.
     * L'animation peut être interrompue par un clic sur le GIF.
     */
    @FXML
    public void AnimationSave() {
        Image gif = new Image(getClass().getResource("ChargeScene.gif").toExternalForm());
        ImageView gifView = new ImageView(gif);
        AdaptSize(gifView, Begin.getScene());

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-0.5);
        colorAdjust.setBrightness(-0.2);
        gifView.setEffect(colorAdjust);

        Begin.getChildren().add(gifView);
        NewLab.setVisible(false);
        ChargeSave.setVisible(false);

        ParallelTransition showGif = getParallelTransition(gifView);

        showGif.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(9));
            pause.setOnFinished(ev -> {
                Image finalFrame = new Image(getClass().getResource("ChargeSceneFinal.png").toExternalForm());
                BGHome.setImage(finalFrame);
                GoBack.setVisible(true);
                GoBack.setOnMouseClicked(mouseEvent -> AnimationSaveBack(null));
                BorderSave.setVisible(true);
                ShowSaves();
                Begin.getChildren().remove(gifView);
            });

            gifView.setOnMouseClicked(mouseEvent -> {
                if (pause.getStatus() == Animation.Status.RUNNING) {
                    pause.stop();
                    pause.getOnFinished().handle(new ActionEvent());
                }
            });
            pause.play();
        });
    }


    /**
     * Crée et lance une animation combinée pour un ImageView, incluant un fondu entrant
     * et un déplacement horizontal aller-retour.
     *
     * @param gifView l'ImageView à animer
     * @return la ParallelTransition qui regroupe les animations
     */
    private static ParallelTransition getParallelTransition(ImageView gifView) {
        TranslateTransition slideRight = new TranslateTransition(Duration.seconds(0.5), gifView);
        slideRight.setFromX(0);
        slideRight.setToX(10);
        TranslateTransition slideBack = new TranslateTransition(Duration.seconds(0.5), gifView);
        slideBack.setFromX(10);
        slideBack.setToX(0);

        SequentialTransition slideSequence = new SequentialTransition(slideRight, slideBack);

        FadeTransition fadeInGif = new FadeTransition(Duration.seconds(0.5), gifView);
        fadeInGif.setFromValue(0);
        fadeInGif.setToValue(1);

        ParallelTransition showGif = new ParallelTransition(fadeInGif, slideSequence);
        showGif.play();
        return showGif;
    }


    /**
     * Joue une animation de retour avec un GIF, puis restaure l'état initial de l'interface.
     * Permet d'exécuter une action optionnelle à la fin de l'animation.
     *
     * @param onFinished action à exécuter une fois l'animation terminée, peut être null
     */
    public void AnimationSaveBack(Runnable onFinished) {
        Image gif = new Image(getClass().getResource("ChargeSceneBackWard.gif").toExternalForm());
        ImageView gifView = new ImageView(gif);
        AdaptSize(gifView, Begin.getScene());
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-0.5);
        colorAdjust.setBrightness(-0.2);
        gifView.setEffect(colorAdjust);

        Begin.getChildren().add(gifView);

        PauseTransition pause = new PauseTransition(Duration.seconds(6.6));
        pause.setOnFinished(ev -> {
            Image finalFrame = new Image(getClass().getResource("home-sans-cy.png").toExternalForm());
            BGHome.setImage(finalFrame);

            NewLab.setVisible(true);
            ChargeSave.setVisible(true);
            Begin.getChildren().remove(gifView);
            GoBack.setVisible(false);
            BorderSave.setVisible(false);
            HBoxSave.getChildren().clear();
            if (onFinished != null) {
                onFinished.run();
            }
        });

        gifView.setOnMouseClicked(mouseEvent -> {
            if (pause.getStatus() == Animation.Status.RUNNING) {
                pause.stop();
                pause.getOnFinished().handle(new ActionEvent());
            }
        });
        pause.play();
    }



    /**
     * Affiche la liste des sauvegardes de labyrinthes depuis la base de données.
     * Chaque sauvegarde est affichée dans un VBox avec ses informations (nom, dimensions)
     * et un bouton permettant de supprimer la sauvegarde.
     * Cliquer sur une sauvegarde lance son chargement avec une animation de transition.
     */
    public void ShowSaves(){
        try(Connection conn = db.connectDatabase()){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Maze");
            while(rs.next()){
                String string = rs.getString("name");
                int id = rs.getInt("id");
                VBox pane = new VBox(5);
                pane.getStyleClass().add("SaveBorder");
                pane.setPrefSize(100, 100);
                pane.setAlignment(Pos.CENTER);
                Label name = new Label(rs.getString("name"));
                Label height = new Label("Largeur: " + rs.getInt("height"));
                Label width = new Label("Longueur: " +rs.getInt("width"));
                Button delete = new Button("Supprimer");
                pane.getChildren().addAll(name, height, width, delete);
                pane.setOnMouseClicked(event -> {
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    AnimationSaveBack(() -> {
                        try {
                            OpenMazeGeneration(stage, () -> controller.ChargeMaze(string));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                });
                delete.setOnMouseClicked(event -> {
                    try (Connection conn2 = db.connectDatabase()){
                        PreparedStatement deleteStmt = conn2.prepareStatement("DELETE FROM Maze WHERE id = ?");
                        deleteStmt.setInt(1, id);
                        deleteStmt.executeUpdate();
                        HBoxSave.getChildren().remove(pane);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                });

                HBoxSave.getChildren().add(pane);
                HBox.setMargin(pane, new Insets(20));
            }
            stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    /**
     * Active la bascule plein écran sur la scène avec la touche F11.
     *
     * @param scene La scène sur laquelle activer la gestion du plein écran.
     */
    private void FullScreen(Scene scene){
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                Stage stage = (Stage) scene.getWindow();
                stage.setFullScreen(!stage.isFullScreen());
                Begin.layout();
            }
        });
    }
}

