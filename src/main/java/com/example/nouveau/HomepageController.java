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

    @FXML
    public void initialize() {
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

        BGHome.setPreserveRatio(false);

        BGHome.fitWidthProperty().bind(Begin.widthProperty());
        BGHome.fitHeightProperty().bind(Begin.heightProperty());

        BorderSave.prefWidthProperty().bind(Begin.widthProperty().multiply(0.8));
        BorderSave.prefHeightProperty().bind(Begin.heightProperty().multiply(0.3));

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

        ActiveReplaceLabel(StartButton, 0.47, 0.6, 0.045);
        ActiveReplaceLabel(CyNapse, 0.47, 0.2, 0.045);
        ActiveReplaceButton(NewLab, 0.30, 0.65, 0.02);
        ActiveReplaceButton(ChargeSave, 0.65, 0.65, 0.02);
        ActiveReplaceAnchorPane(BorderSave, 0.5, 0.6);
        ActiveReplaceButton(GoBack, 0.15, 0.42, 0.02);
    }

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



    private void AdaptSize(ImageView view, Scene scene) {
        view.fitWidthProperty().bind(scene.widthProperty());
        view.fitHeightProperty().bind(scene.heightProperty());
        view.setPreserveRatio(false);
    }

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

    @FXML
    public void OpenMazeGeneration(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        OpenMazeGeneration(stage, null);
    }
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

