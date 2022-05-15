package me.logicologist.wordiple.client.gui.controllers.game;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import me.logicologist.wordiple.client.gui.controllers.AttachableAdapter;
import me.logicologist.wordiple.client.gui.controllers.overlays.OverlayController;

import java.net.URL;
import java.util.ResourceBundle;

public class GameTextOverlayController extends AttachableAdapter {

    @FXML
    private AnchorPane movablePane;

    @FXML
    private Label textLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setAttachment(movablePane);
    }

    public void transitionIn() {
        double duration = 0.8;

        movablePane.setOpacity(0);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.opacityProperty(), 0)),
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.scaleXProperty(), 1)),
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.scaleYProperty(), 1)),
                new KeyFrame(Duration.seconds(duration/2), new KeyValue(movablePane.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(duration), new KeyValue(movablePane.opacityProperty(), 0)),
                new KeyFrame(Duration.seconds(duration), new KeyValue(movablePane.scaleXProperty(), 1.25)),
                new KeyFrame(Duration.seconds(duration), new KeyValue(movablePane.scaleYProperty(), 1.25))
        );

        timeline.play();

        timeline.setOnFinished(x -> {
            super.detach();
        });
    }

    public void setTextLabel(String text) {
        textLabel.setText(text);
    }
}
