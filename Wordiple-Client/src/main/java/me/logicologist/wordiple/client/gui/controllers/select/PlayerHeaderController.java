package me.logicologist.wordiple.client.gui.controllers.select;

import com.jcraft.jsch.Session;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.animations.PopAnimation;
import me.logicologist.wordiple.client.gui.animations.ShakeAnimation;
import me.logicologist.wordiple.client.gui.controllers.AttachableAdapter;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.SessionManager;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
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

    private boolean midAction = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;

        this.setUsername(SessionManager.getInstance().getUsername());
        this.setLevelUnanimated(SessionManager.getInstance().getLevel());
        this.setBarPercentage((double) SessionManager.getInstance().getCurrentXp() / SessionManager.getInstance().getNeededXp(), null);

        super.setAttachment(headerPane);

        profileButton.setOnAction(event -> {
            if (midAction) return;
            midAction = true;
            if (!profileButton.isHover()) return;

            GUIManager.getInstance().showProfileOverlay(SessionManager.getInstance().getUsername(), () -> midAction = false);
        });
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

    public void setExperience(int newExperience, int newLevel, int newNeededXp) {
        int currentLevel = SessionManager.getInstance().getLevel();
        for (int level = currentLevel; level < newLevel; level++) {
            int finalLevel = level;
            WordipleClient.getExecutor().schedule(() -> {
                SessionManager.getInstance().setCurrentXp(SessionManager.getInstance().getNeededXp());
                setBarPercentage(1, () -> {
                    setLevelUp(finalLevel, () -> {
                        SessionManager.getInstance().setNeededXp(newNeededXp);
                        if (finalLevel - 1 == newLevel) {
                            SessionManager.getInstance().setCurrentXp(newExperience);
                            return;
                        }
                        SessionManager.getInstance().setCurrentXp(SessionManager.getInstance().getNeededXp());
                    });
                });
            }, (newLevel - level - 1) * 5L, TimeUnit.SECONDS);
            this.setBarPercentage(1, null);
        }
        WordipleClient.getExecutor().schedule(() -> {
            this.setBarPercentage((double) SessionManager.getInstance().getCurrentXp() / SessionManager.getInstance().getNeededXp(), null);
        }, newLevel - currentLevel * 5L, TimeUnit.SECONDS);
        this.setBarPercentage((double) newExperience / newNeededXp, null);
        this.setLevelUnanimated(newLevel);
    }

    public void setLevelUp(int newLevel, Runnable runAfter) {
        currentLevelLabel.setText(String.valueOf(newLevel));
        newLevelLabel.setText(String.valueOf(newLevel + 1));
        new PopAnimation(currentLevelLabel, 2, 1.4);
        new PopAnimation(newLevelLabel, 2, 1.4);
        SessionManager.getInstance().setLevel(newLevel);
        WordipleClient.getExecutor().schedule(() -> {
            runAfter.run();
            clearBar();
        }, 2, TimeUnit.SECONDS);
    }

    public void clearBar() {
        new ShakeAnimation(2, levelProgressBar.layoutYProperty(), 1);
        levelProgressBar.setProgress(0);
    }

    public void setUsername(String username) {
        this.nameLabel.setText(username);
    }

    public void setLevelUnanimated(int level) {
        this.currentLevelLabel.setText(String.valueOf(level));
        this.newLevelLabel.setText(String.valueOf(level + 1));
    }
}
