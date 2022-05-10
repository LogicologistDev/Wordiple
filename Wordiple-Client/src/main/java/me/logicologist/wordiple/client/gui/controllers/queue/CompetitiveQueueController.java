package me.logicologist.wordiple.client.gui.controllers.queue;

import com.olziedev.olziesocket.framework.PacketArguments;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CompetitiveQueueController extends QueueController {

    @FXML
    private Label rankLabel;

    @FXML
    private Label ratingLabel;

    public void setInfo(PacketArguments playerInfo) {
        rankLabel.setText(playerInfo.get("current_rank", String.class));
        ratingLabel.setText(playerInfo.get("current_rating", String.class) + " Word Rating");
    }
}
