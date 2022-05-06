package me.logicologist.wordiple.client.gui.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import me.logicologist.wordiple.client.manager.SessionManager;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerHeaderController extends AttachableAdapter {

    @FXML
    private AnchorPane headerPane;

    @FXML
    private Button profileButton;

    @FXML
    private Label nameLabel;

    @FXML
    private Label currentLevelLabel;

    @FXML
    private Label newLevelLabel;

    @FXML
    private Label levelProgressLabel;

    @FXML
    private ProgressBar levelProgressBar;

    public static PlayerHeaderController instance = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        super.setAttachment(headerPane);
    }

    public void setBarPercentage(double percentage, Runnable runAfter) {
        this.levelProgressLabel.setText((int) Math.max(this.levelProgressBar.getProgress(), 0) * SessionManager.getInstance().getNeededXp() + " XP / " + SessionManager.getInstance().getNeededXp() + " XP");

        Duration duration = Duration.seconds(Math.abs(percentage - Math.max(this.levelProgressBar.getProgress(), 0)) * 2);
        AtomicInteger frameCounter = new AtomicInteger();

        AtomicBoolean finished = new AtomicBoolean(false);

        int maxFrames = (int) (duration.toSeconds() / 0.01);
        int finalXp = (int) (SessionManager.getInstance().getNeededXp() * percentage);
        Timeline textTimeline = new Timeline(new KeyFrame(Duration.seconds(0.01), e -> {
            if (finished.get()) return;
            frameCounter.getAndIncrement();
            this.levelProgressLabel.setText((int) (finalXp * ((double) frameCounter.get() / maxFrames)) + " XP / " + SessionManager.getInstance().getNeededXp() + " XP");
        }));
        textTimeline.setCycleCount(maxFrames);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(this.levelProgressBar.progressProperty(), this.levelProgressBar.getProgress())),
                new KeyFrame(Duration.ZERO, e -> {
                    textTimeline.playFromStart();
                }),
                new KeyFrame(duration, new KeyValue(this.levelProgressBar.progressProperty(), percentage)
        ));
        timeline.play();
        timeline.setOnFinished(e -> {
            finished.set(true);
            textTimeline.stop();
            this.levelProgressLabel.setText(SessionManager.getInstance().getCurrentXp() + " XP / " + SessionManager.getInstance().getNeededXp() + " XP");
            if (runAfter != null) runAfter.run();
        });

    }

    public void setUsername(String username) {
        this.nameLabel.setText(username);
    }

    public void setLevel(int level) {
        this.currentLevelLabel.setText(String.valueOf(level));
        this.newLevelLabel.setText(String.valueOf(level + 1));
    }
}
