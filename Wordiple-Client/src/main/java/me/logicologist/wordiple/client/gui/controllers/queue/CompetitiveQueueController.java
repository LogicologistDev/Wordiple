package me.logicologist.wordiple.client.gui.controllers.queue;

import com.olziedev.olziesocket.framework.PacketArguments;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.common.queue.QueueType;

public class CompetitiveQueueController extends QueueController {

    @FXML
    private Label rankLabel;

    @FXML
    private Label ratingLabel;

    @FXML
    private Label rankIconLabel;

    public void setInfo(PacketArguments playerInfo) {
        rankLabel.setText(playerInfo.get("current_rank", String.class));
        ratingLabel.setText(playerInfo.get("current_rating", String.class) + " Word Rating");
        GUIManager.setRankStyleClass(rankIconLabel, playerInfo.get("current_rank", String.class));
    }

    @Override
    public QueueType getQueueType() {
        return QueueType.COMPETITIVE;
    }
}
