package me.logicologist.wordiple.client.gui.controllers.overlays;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import me.logicologist.wordiple.client.gui.controllers.AttachableAdapter;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmExitOverlayController extends AttachableAdapter {

    //TODO: Add a confirmation dialog. Yes Or no.

    @FXML
    public AnchorPane movablePane;

    @FXML
    public Button backgroundButton;

    private boolean midAction = false;
    private OverlayController overlayController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setAttachment(movablePane);
        backgroundButton.setOnAction(x -> {
            if (midAction) return;
            midAction = true;
            transitionOut();
        });
    }

    public void transitionIn(OverlayController overlayController, Runnable runAfter) {
        this.overlayController = overlayController;
        midAction = true;
        Duration rotateDuration = Duration.seconds(0.5);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.layoutXProperty(), -100)), // initial rotate
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.layoutYProperty(), -500)), // initial rotate
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.rotateProperty(), -25)), // initial rotate
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.opacityProperty(), 0)), // initial rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.layoutXProperty(), 0)),// end value of rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.layoutYProperty(), 0)), // initial rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.rotateProperty(), 0)), // end value of rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.opacityProperty(), 1)) // initial rotate
        );
        timeline.play();
        timeline.setOnFinished(x -> {
            midAction = false;
            if (runAfter != null) runAfter.run();
        });
    }

    public void transitionOut() {
        Duration rotateDuration = Duration.seconds(0.5);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.layoutXProperty(), 0)),// end value of rotate
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.layoutYProperty(), 0)), // initial rotate
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.rotateProperty(), 0)), // end value of rotate
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.opacityProperty(), 1)), // initial rotate
                new KeyFrame(Duration.seconds(0.3), e -> {
                    this.overlayController.transitionOut();
                }),
                new KeyFrame(rotateDuration, new KeyValue(movablePane.layoutXProperty(), 100)), // initial rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.layoutYProperty(), 500)), // initial rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.rotateProperty(), 25)), // initial rotate
                new KeyFrame(rotateDuration, new KeyValue(movablePane.opacityProperty(), 0)) // initial rotate
        );
        timeline.play();
        timeline.setOnFinished(x -> {
            overlayController.detach();
            super.detach();
        });
    }
}
