package com.example.nouveau;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

public class HomepageController {
    @FXML private AnchorPane Begin;
    @FXML private ImageView BGHome;
    @FXML private Label StartButton;
    @FXML private Button ChargeSave;
    @FXML private Button NewLab;
    private Database db;

    private FadeTransition fade;

    @FXML
    public void initialize() {
        db = new Database();
        db.createDatabase();
        db.createTable();

        ColorAdjust colorAdjust = new ColorAdjust();
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
        fade.play();
    }

    @FXML
    void NewGame(MouseEvent event) {
        Begin.setOnMouseClicked(null);
        fade.stop();
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

        NewLab.setLayoutX(230);
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
    void OpenMazeGeneration(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        HelloController controller = fxmlLoader.getController();
        controller.setDatabase(this.db);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }
}

