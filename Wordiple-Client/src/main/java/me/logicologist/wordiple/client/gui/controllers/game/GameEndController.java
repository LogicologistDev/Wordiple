package me.logicologist.wordiple.client.gui.controllers.game;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.AttachableAdapter;
import me.logicologist.wordiple.client.gui.controllers.overlays.OverlayController;
import me.logicologist.wordiple.client.gui.controllers.select.PlayerHeaderController;
import me.logicologist.wordiple.client.manager.GUIManager;

import java.net.URL;
import java.util.ResourceBundle;

public class GameEndController extends AttachableAdapter {

    @FXML
    private Button gameSelectButton;

    @FXML
    private Label loseLabel;

    @FXML
    private AnchorPane movablePane;

    @FXML
    private Label rankDisplay;

    @FXML
    private Label rankLabel;

    @FXML
    private Label ratingPositiveChangeLabel;

    @FXML
    private Label ratingNegativeChangeLabel;

    @FXML
    private Label ratingToRankupLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label totalRatingLabel;

    @FXML
    private Label winLabel;

    private boolean midAction;
    private OverlayController overlayController;
    private int newExperience;
    private int newLevel;
    private int requiredExperience;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setAttachment(movablePane);
        this.gameSelectButton.setOnAction(event -> {
            this.gameSelectButton.getScene().getWindow().hide();
        });
    }

    public void transitionIn(OverlayController overlayController, Runnable runAfter) {
        this.overlayController = overlayController;

        midAction = true;

        double duration = 2;

        movablePane.setOpacity(0);
        movablePane.setScaleX(1.5);
        movablePane.setScaleY(1.5);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.opacityProperty(), 0)),
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.scaleXProperty(), 1.5)),
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.scaleYProperty(), 1.5)),
                new KeyFrame(Duration.seconds(duration), new KeyValue(movablePane.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(duration), new KeyValue(movablePane.scaleXProperty(), 1)),
                new KeyFrame(Duration.seconds(duration), new KeyValue(movablePane.scaleYProperty(), 1))
        );

        timeline.play();
        timeline.setOnFinished(x -> {
            midAction = false;
            if (runAfter != null) runAfter.run();
        });
    }

    public void transitionOut() {
        midAction = true;
        double duration = 2;

        movablePane.setOpacity(1);
        movablePane.setScaleX(1);
        movablePane.setScaleY(1);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.opacityProperty(), 0)),
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.scaleXProperty(), 1)),
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.scaleYProperty(), 1)),
                new KeyFrame(Duration.seconds(1.7), e -> {
                    this.overlayController.transitionOut();
                }),
                new KeyFrame(Duration.seconds(duration), new KeyValue(movablePane.opacityProperty(), 0)),
                new KeyFrame(Duration.seconds(duration), new KeyValue(movablePane.scaleXProperty(), 0.5)),
                new KeyFrame(Duration.seconds(duration), new KeyValue(movablePane.scaleYProperty(), 0.5))
        );

        timeline.play();
        timeline.setOnFinished(x -> {
            GUIManager.getInstance().resetGameController();
            GUIManager.getInstance().startSwipeTransition(null, () -> {
                GUIManager.getInstance().showGameSelectScreen(false);
                WordipleClient.getExecutor().schedule(() -> {
                    Platform.runLater(() -> {
                        PlayerHeaderController.instance.setExperience(newExperience, newLevel, requiredExperience);
                    });
                }, 1, java.util.concurrent.TimeUnit.SECONDS);
            });
        });
    }

    public void setData(boolean winner, String scoreDisplay, int newExperience, int newLevel, int requiredExperience) {
        this.newExperience = newExperience;
        this.newLevel = newLevel;
        this.requiredExperience = requiredExperience;
        this.scoreLabel.setText(scoreDisplay);
        if (winner) {
            loseLabel.setVisible(false);
            return;
        }
        winLabel.setVisible(false);
    }

    public void setCompetitiveData(int totalRating, int ratingChange, String rank, int ratingToRankup) {
        this.totalRatingLabel.setText(totalRating + " WR");
        if (ratingChange >= 0) {
            this.ratingNegativeChangeLabel.setVisible(false);
            this.ratingPositiveChangeLabel.setText("+" + ratingChange + " WR");
        }
        if (ratingChange < 0) {
            this.ratingPositiveChangeLabel.setVisible(false);
            this.ratingNegativeChangeLabel.setText(ratingChange + " WR");
        }
        GUIManager.setRankStyleClass(rankDisplay, rank);
        this.ratingToRankupLabel.setText(String.valueOf(ratingToRankup));
    }
}
