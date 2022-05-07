package me.logicologist.wordiple.client.gui.controllers.overlays;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import me.logicologist.wordiple.client.gui.controllers.AttachableAdapter;

import java.net.URL;
import java.util.ResourceBundle;

public class OverlayController extends AttachableAdapter {

    @FXML
    public AnchorPane movablePane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setAttachment(movablePane);
        movablePane.setOpacity(0);
    }

    public void transitionIn() {
        Duration rotateDuration = Duration.seconds(0.2);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.opacityProperty(), 0)), // initial rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.opacityProperty(), 1)) // initial rotate
        );
        timeline.play();
    }

    public void transitionOut() {
        Duration rotateDuration = Duration.seconds(0.2);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.opacityProperty(), movablePane.getOpacity())),// end value of rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.opacityProperty(), 0)) // initial rotate
        );
        timeline.play();
        timeline.setOnFinished(x -> {
            super.detach();
        });
    }
}
