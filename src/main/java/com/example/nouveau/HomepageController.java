package com.example.nouveau; // Define the project package
// Importations of the JavaFX libraries
import javafx.animation.*; // For the transitions and animations
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
import java.sql.*; // For the database

public class HomepageController {
    //FXML elements
    @FXML private AnchorPane Begin; // AnchorPane for the main scene
    @FXML private AnchorPane BorderSave; // AnchorPane for the save section
    @FXML private ImageView BGHome; // ImageView for the background image
    @FXML private Label StartButton; // Label for the start button
    @FXML private Button ChargeSave; // Button to load a saved maze
    @FXML private Button NewLab; // Button to create a new maze
    @FXML private Label CyNapse; // Label for the title
    @FXML private HBox HBoxSave; // HBox for the saved mazes
    @FXML private Button GoBack; // Button to go back to the main menu

    private HelloController controller; 
    private Database db;

    @FXML
    //This methode initializes the homepage by creating a database,
    //setting the full screen mode, and adapting the size of the elements
    public void initialize() {
        //Initialize the database
        db = new Database(); // Create a new Database instance
        db.createDatabase(); // Create the database if it doesn't exist
        db.createTable(); // Creat the tables of the mazes
        
        Scene scene = Begin.getScene(); // Retrieve the current scene

        //If the scene is already initialized, set the full screen mode
        //If not, wait for the scene to be available and then set the full screen mode
        if (scene != null) {
            FullScreen(scene);
        } else {
            Begin.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    FullScreen(newScene);
                }
            });
        }

        //Link the size of the image with the AnchorPane
        BGHome.setPreserveRatio(false);
        BGHome.fitWidthProperty().bind(Begin.widthProperty());
        BGHome.fitHeightProperty().bind(Begin.heightProperty());
        
        //Link the size of the AnchorPane with the size of its parent
        BorderSave.prefWidthProperty().bind(Begin.widthProperty().multiply(0.8));
        BorderSave.prefHeightProperty().bind(Begin.heightProperty().multiply(0.3));

        //Adapt the size of the elements with the AnchorPane
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

        //Readapt the size of the elements when the AnchorPane is resized

        ActiveReplaceLabel(StartButton, 0.47, 0.6, 0.045);
        ActiveReplaceLabel(CyNapse, 0.47, 0.2, 0.045);
        ActiveReplaceButton(NewLab, 0.30, 0.65, 0.02);
        ActiveReplaceButton(ChargeSave, 0.65, 0.65, 0.02);
        ActiveReplaceAnchorPane(BorderSave, 0.5, 0.6);
        ActiveReplaceButton(GoBack, 0.15, 0.42, 0.02);
    }

    //Methodes that move the elements and adapt their size according to the size of the page
    
    //Labels
    private void ReplaceLabel(Label label, double x, double y, double fontSizeRatio) {
        label.setStyle("-fx-font-size: " + Begin.getWidth() * fontSizeRatio + "px;");
        Platform.runLater(() -> {
                double centerX = Begin.getWidth() * x;
                double centerY = Begin.getHeight() * y;

                label.setLayoutX(centerX - label.getWidth() / 2);
                label.setLayoutY(centerY - label.getHeight() / 2);
        });
    }
    private void ActiveReplaceLabel(Label label, double x, double y, double FontSize) {
        label.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceLabel(label, x, y, FontSize));
        });

        label.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceLabel(label, x, y, FontSize));
        });
    }

    //Buttons
    private void ReplaceButton(Button btn, double x, double y, double fontSizeRatio) {
        btn.setStyle("-fx-font-size: " + Begin.getWidth() * fontSizeRatio + "px;");

            Platform.runLater(() -> {
                double centerX = Begin.getWidth() * x;
                double centerY = Begin.getHeight() * y;

                btn.setLayoutX(centerX - btn.getWidth() / 2);
                btn.setLayoutY(centerY - btn.getHeight() / 2);
            });
    }
    private void ActiveReplaceButton(Button btn, double x, double y, double FontSize) {
        btn.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceButton(btn, x, y, FontSize));
        });

        btn.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceButton(btn, x, y, FontSize));
        });
    }

    //AnchorPane
    private void ReplaceAnchorPane(AnchorPane anchor, double x, double y) {
        double centerX = Begin.getWidth() * x;
        double centerY = Begin.getHeight() * y;

        anchor.setLayoutX(centerX - anchor.getWidth() / 2);
        anchor.setLayoutY(centerY - anchor.getHeight() / 2);
    }
    private void ActiveReplaceAnchorPane(AnchorPane anchor, double x, double y) {
        anchor.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceAnchorPane(anchor, x, y));
        });

        anchor.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> ReplaceAnchorPane(anchor, x, y));
        });
    }

    //ImageView
    private void AdaptSize(ImageView view, Scene scene) {
        view.fitWidthProperty().bind(scene.widthProperty());
        view.fitHeightProperty().bind(scene.heightProperty());
        view.setPreserveRatio(false);
    }

    //Start a new game
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

    //Show the Level's buttons
    private void showLevelButtons() {

        //Make the buttons NewLab and ChargeSave appear progressively
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

    //Start the generation of a new maze
    @FXML
    public void OpenMazeGeneration(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        OpenMazeGeneration(stage, null);
    }
    public void OpenMazeGeneration(Stage stage, Runnable onFinished) throws IOException {
        //Show an animation GIF for 4 seconds and then load hello-view.fxml
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

    //Animation when loading the saved maze
    @FXML
    public void AnimationSave() {
        //Shox an animation GIF and desaturates the background image
        //After 9 seconds or if the screen is clicked, it shows the available saved mazes
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

    private static ParallelTransition getParallelTransition(ImageView gifView) {
        //Slide aniamtion + melting effect of the gif
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

    //Animation when going back to the main menu from the saved maze screen
    public void AnimationSaveBack(Runnable onFinished) {
        //Inversed animation of the previous one
        //After 6.6 seconds or if the screen is clicked, it shows the main menu
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

    //Show the saved mazes from the database.
    //It will send a request to the table Maze, for each save it will create a VBox with the name, height and width of the maze
    //If we click on it it will load the maze, if we click on the delete button it will delete the maze from the database
    //and remove the VBox from the HBox
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
    
    //This method is used to set the full screen mode of the scene
    //It will set the full screen mode when the F11 key is pressed
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

