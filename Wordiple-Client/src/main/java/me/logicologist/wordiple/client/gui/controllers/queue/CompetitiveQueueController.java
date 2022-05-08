package me.logicologist.wordiple.client.gui.controllers.queue;

import com.olziedev.olziesocket.framework.PacketArguments;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.gui.controllers.transitions.FadeHorizontalTransitionAdapter;

import java.net.URL;
import java.util.ResourceBundle;

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setPane(movablePane);
    }

    public void setInfo(PacketArguments playerInfo, PacketArguments queueInfo) {
        activeLabel.setText(queueInfo.get("active", Integer.class) + " Active");
        rankLabel.setText(playerInfo.get("current_rank", String.class));
        ratingLabel.setText(playerInfo.get("current_rating", String.class) + " Word Rating");
    }
}
