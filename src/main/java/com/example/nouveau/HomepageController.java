package com.example.nouveau;
import javafx.animation.*;
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
    private Label GoBack;

    private HelloController controller;
    private Database db;

    @FXML
    public void initialize() {
        db = new Database();
        db.createDatabase();
        db.createTable();

        /*ColorAdjust colorAdjust = new ColorAdjust();
        BGHome.setEffect(colorAdjust);

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(5),
                        new KeyValue(colorAdjust.saturationProperty(), -0.1)
                )
        );
        colorAdjust.setSaturation(-0.7);

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();

        fade = new FadeTransition(Duration.seconds(2), StartButton);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play(); */
    }

    @FXML
    void NewGame() {
        Begin.setOnMouseClicked(null);
        //fade.stop();
        FadeTransition finalFade = new FadeTransition(Duration.seconds(1), StartButton);
        finalFade.setFromValue(StartButton.getOpacity());
        finalFade.setToValue(0);
        finalFade.setOnFinished(e -> {
            Begin.getChildren().remove(StartButton);
            showLevelButtons();
        });
        finalFade.play();
    }

    private void showLevelButtons() {

        double targetY1 = 370;
        double targetY2 = 370;

        NewLab.setLayoutX(150);
        NewLab.setLayoutY(700);
        ChargeSave.setLayoutX(500);
        ChargeSave.setLayoutY(700);

        TranslateTransition slideUp1 = new TranslateTransition(Duration.seconds(1), NewLab);
        slideUp1.setFromY(0);
        slideUp1.setToY(targetY1 - 700);

        TranslateTransition slideUp2 = new TranslateTransition(Duration.seconds(1), ChargeSave);
        slideUp2.setFromY(0);
        slideUp2.setToY(targetY2 - 700);

        slideUp1.play();
        slideUp2.play();
    }

    @FXML
    public void OpenMazeGeneration(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        OpenMazeGeneration(stage, null);
    }
    public void OpenMazeGeneration(Stage stage, Runnable onFinished) throws IOException {
        Image gif = new Image(getClass().getResource("StartNewMaze.gif").toExternalForm());
        ImageView gifView = new ImageView(gif);
        gifView.setFitWidth(1000);
        gifView.setFitHeight(600);
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
        gifView.setFitWidth(1000);
        gifView.setFitHeight(600);

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-0.5);
        colorAdjust.setBrightness(-0.2);
        gifView.setEffect(colorAdjust);

        Begin.getChildren().add(gifView);
        Begin.getChildren().removeAll(NewLab, ChargeSave);

        ParallelTransition showGif = getParallelTransition(gifView);

        showGif.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(9));
            pause.setOnFinished(ev -> {
                Image finalFrame = new Image(getClass().getResource("ChargeSceneFinal.png").toExternalForm());
                BGHome.setImage(finalFrame);
                CyNapse.setLayoutX(410);
                CyNapse.setLayoutY(55);
                GoBack = new Label("Retour");
                GoBack.setLayoutX(150);
                GoBack.setLayoutY(260);
                GoBack.getStyleClass().add("Button");
                Begin.getChildren().add(GoBack);
                GoBack.setOnMouseClicked(mouseEvent -> AnimationSaveBack(null));

                BorderSave.setLayoutX(125);
                BorderSave.setLayoutY(300);
                if(!Begin.getChildren().contains(BorderSave)) {
                    Begin.getChildren().add(BorderSave);
                }
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
        gifView.setFitWidth(1000);
        gifView.setFitHeight(600);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-0.5);
        colorAdjust.setBrightness(-0.2);
        gifView.setEffect(colorAdjust);

        Begin.getChildren().add(gifView);

        PauseTransition pause = new PauseTransition(Duration.seconds(6.6));
        pause.setOnFinished(ev -> {
            Image finalFrame = new Image(getClass().getResource("home-sans-cy.png").toExternalForm());
            BGHome.setImage(finalFrame);

            CyNapse.setLayoutX(325);
            CyNapse.setLayoutY(90);
            Begin.getChildren().addAll(NewLab, ChargeSave);
            Begin.getChildren().removeAll(GoBack, BorderSave, gifView);
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
}

