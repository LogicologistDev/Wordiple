package me.logicologist.wordiple.client.gui.controllers.queue;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.LoadScreenController;
import me.logicologist.wordiple.client.gui.controllers.transitions.FadeHorizontalTransitionAdapter;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.SoundManager;
import me.logicologist.wordiple.client.sound.SoundType;
import me.logicologist.wordiple.common.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public abstract class QueueController extends FadeHorizontalTransitionAdapter {
    @FXML
    protected AnchorPane movablePane;

    @FXML
    protected Button enterButton;

    @FXML
    protected Button backButton;

    @FXML
    protected Label timeLabel;

    @FXML
    protected Label activeLabel;

    private String queueStyle;
    private String dequeueStyle;

    public boolean midAction = false;
    private ScheduledFuture<?> future = null;
    private boolean inQueue = false;

    public void setActive(int active) {
        activeLabel.setText(active + " Active");
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setPane(movablePane);
        backButton.setOnAction(event -> {
            if (midAction) return;
            midAction = true;

            SoundManager.getInstance().getSound(SoundType.BUTTON_CLICK).play();
            super.transitionOut(() -> {
                GUIManager.getInstance().showGameSelectScreen(true);
            });
            if (future != null) future.cancel(true);
        });

        enterButton.setOnAction(event -> {
            if (midAction) return;
            midAction = true;

            if (!inQueue) {
                LoadScreenController loadScreenController = GUIManager.getInstance().showLoadScreen("Joining Queue...");

                inQueue = true;
                enterButton.getStyleClass().clear();
                enterButton.getStyleClass().add(dequeueStyle);
                WordipleClient.getExecutor().schedule(() -> {
                    loadScreenController.remove(this::startTimer);
                }, 1, TimeUnit.SECONDS);
                return;
            }

            LoadScreenController loadScreenController = GUIManager.getInstance().showLoadScreen("Leaving Queue...");

            inQueue = false;
            enterButton.getStyleClass().clear();
            enterButton.getStyleClass().add(queueStyle);
            WordipleClient.getExecutor().schedule(() -> {
                loadScreenController.remove(this::stopTimer);
            }, 1, TimeUnit.SECONDS);
        });
    }

    public void startTimer() {
        midAction = false;
        AtomicLong openedTime = new AtomicLong();
        future = WordipleClient.getExecutor().scheduleAtFixedRate(() -> {
            Platform.runLater(() -> timeLabel.setText("Queue Elapsed: " + Utils.formatShortTime(openedTime.getAndIncrement())));
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stopTimer() {
        future.cancel(true);
        timeLabel.setText("Not in queue!");
        midAction = false;
    }

    public void setQueueButtonStyles(String queue, String dequeue) {
        this.queueStyle = queue;
        this.dequeueStyle = dequeue;
    }
}
