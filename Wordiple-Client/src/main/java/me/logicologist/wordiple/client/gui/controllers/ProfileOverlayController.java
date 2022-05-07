package me.logicologist.wordiple.client.gui.controllers;

import com.olziedev.olziesocket.framework.PacketArguments;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileOverlayController extends AttachableAdapter {


    @FXML
    public AnchorPane movablePane;

    @FXML
    public Button backgroundButton;

    @FXML
    public TextField gamesPlayedField;

    @FXML
    public Label usernameLabel;

    @FXML
    public TextField winsField;

    @FXML
    public TextField lossesField;

    @FXML
    public TextField playtimeField;

    @FXML
    public TextField currentSessionField;

    @FXML
    public TextField seasonField;

    @FXML
    public TextField currentRankField;

    @FXML
    public TextField currentRatingField;

    @FXML
    public TextField highestRankField;

    @FXML
    public TextField highestRatingField;

    @FXML
    public TextField solveTimeField;

    @FXML
    public TextField openerField;

    @FXML
    public TextField guessesField;

    @FXML
    public TextField levelField;

    @FXML
    public TextField xpField;

    private boolean midAction = false;
    private OverlayController overlayController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setAttachment(movablePane);
        backgroundButton.setOnAction(x -> {
            if (midAction) return;
            midAction = true;
            transitionOut();
        });
    }

    public void transitionIn(OverlayController overlayController, Runnable runAfter) {
        this.overlayController = overlayController;
        midAction = true;
        Duration rotateDuration = Duration.seconds(0.5);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.layoutYProperty(), -200)), // initial rotate
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.opacityProperty(), 0)), // initial rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.layoutYProperty(), 0)), // initial rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.opacityProperty(), 1)) // initial rotate
        );
        timeline.play();
        timeline.setOnFinished(x -> {
            midAction = false;
            if (runAfter != null) runAfter.run();
        });
    }

    public void transitionOut() {
        Duration rotateDuration = Duration.seconds(0.5);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.layoutYProperty(), movablePane.getLayoutY())), // initial rotate
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.opacityProperty(), movablePane.getOpacity())), // initial rotate
                new KeyFrame(Duration.seconds(0.4), e -> {
                    this.overlayController.transitionOut();
                }),
                new KeyFrame(rotateDuration, new KeyValue(movablePane.layoutYProperty(), 200)), // initial rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.opacityProperty(), 0)) // initial rotate
        );
        timeline.play();
        timeline.setOnFinished(x -> {
            super.detach();
        });
    }

    public void setData(PacketArguments args) {
        usernameLabel.setText(args.get("username", String.class));
        gamesPlayedField.setText(args.get("games_played", String.class));
        winsField.setText(args.get("wins", String.class));
        lossesField.setText(args.get("losses", String.class));
        playtimeField.setText(args.get("playtime", String.class));
        currentSessionField.setText(args.get("curren_session", String.class));
        seasonField.setText(args.get("season", String.class));
        currentRankField.setText(args.get("current_rank", String.class));
        currentRatingField.setText(args.get("current_rating", String.class));
        highestRankField.setText(args.get("highest_rank", String.class));
        highestRatingField.setText(args.get("highest_rating", String.class));
        solveTimeField.setText(args.get("solve_time", String.class));
        openerField.setText(args.get("opener", String.class));
        guessesField.setText(args.get("guesses", String.class));
        levelField.setText(args.get("level", String.class));
        xpField.setText(args.get("total_xp", String.class));
    }
}
