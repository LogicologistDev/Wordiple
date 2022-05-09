package me.logicologist.wordiple.client.gui.controllers.queue;

import com.olziedev.olziesocket.framework.PacketArguments;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.WordipleClient;
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

public class CompetitiveQueueController extends FadeHorizontalTransitionAdapter {

    @FXML
    private AnchorPane movablePane;

    @FXML
    private Button enterButton;

    @FXML
    private Button backButton;

    @FXML
    private Label timeLabel;

    @FXML
    private Label activeLabel;

    @FXML
    private Label rankLabel;

    @FXML
    private Label ratingLabel;

    private boolean midAction = false;
    private ScheduledFuture<?> future = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setPane(movablePane);
        backButton.setOnAction(event -> {
            if (midAction) return;
            midAction = true;

            SoundManager.getInstance().getSound(SoundType.BUTTON_CLICK).play();
            super.transitionOut(() -> {
                GUIManager.getInstance().showGameSelectScreen(true);
            });
            future.cancel(true);
        });
        AtomicLong openedTime = new AtomicLong();
        future = WordipleClient.getExecutor().scheduleAtFixedRate(() -> {
            Platform.runLater(() -> timeLabel.setText("Queue Elapsed: " + Utils.formatShortTime(openedTime.getAndIncrement())));
        }, 0, 1, TimeUnit.SECONDS);
    }



    public void setInfo(PacketArguments playerInfo, PacketArguments queueInfo) {
        activeLabel.setText(queueInfo.get("active", Integer.class) + " Active");
        rankLabel.setText(playerInfo.get("current_rank", String.class));
        ratingLabel.setText(playerInfo.get("current_rating", String.class) + " Word Rating");
    }
}
