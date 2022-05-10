package me.logicologist.wordiple.client.gui.controllers.overlays;

import com.olziedev.olziesocket.framework.PacketArguments;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.AttachableAdapter;
import me.logicologist.wordiple.common.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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

    private ScheduledFuture<?> future = null;
    private PacketArguments arguments;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setAttachment(movablePane);
        movablePane.setLayoutY(-200);
        movablePane.setOpacity(0);
        movablePane.setOnMouseClicked(x -> {
            if (midAction || x.getButton() != MouseButton.PRIMARY) return;
            midAction = true;
            transitionOut();
        });
        movablePane.setOnKeyReleased(event -> {
            if (midAction) return;
            midAction = true;

            switch (event.getCode()) {
                case ESCAPE:
                    transitionOut();
                    return;
            }
        });
        backgroundButton.setOnAction(x -> {
            if (midAction) return;
            midAction = true;
            transitionOut();
        });
        AtomicLong openedTime = new AtomicLong();
        future = WordipleClient.getExecutor().scheduleAtFixedRate(() -> {
            playtimeField.setText(Utils.formatShortTime(arguments.get("playtime_raw", Long.class) + openedTime.get()));
            currentSessionField.setText(Utils.formatShortTime(arguments.get("current_session_raw", Long.class) + openedTime.get()));
            openedTime.incrementAndGet();
        }, 1, 1, TimeUnit.SECONDS);
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
            this.movablePane.requestFocus();
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

    @Override
    public void detach() {
        super.detach();
        if (future != null) future.cancel(true);
        this.arguments = null;
    }

    public void setData(PacketArguments arguments) {
        this.arguments = arguments;
        usernameLabel.setText(arguments.get("username", String.class));
        gamesPlayedField.setText(arguments.get("games_played", String.class));
        winsField.setText(arguments.get("wins", String.class));
        lossesField.setText(arguments.get("losses", String.class));
        playtimeField.setText(arguments.get("playtime", String.class));
        currentSessionField.setText(arguments.get("current_session", String.class));
        seasonField.setText(arguments.get("season", String.class));
        currentRankField.setText(arguments.get("current_rank", String.class));
        currentRatingField.setText(arguments.get("current_rating", String.class));
        highestRankField.setText(arguments.get("highest_rank", String.class));
        highestRatingField.setText(arguments.get("highest_rating", String.class));
        solveTimeField.setText(arguments.get("solve_time", String.class));
        openerField.setText(arguments.get("opener", String.class));
        guessesField.setText(arguments.get("guesses", String.class));
        levelField.setText(arguments.get("level", String.class));
        xpField.setText(arguments.get("total_xp", String.class));
    }
}
