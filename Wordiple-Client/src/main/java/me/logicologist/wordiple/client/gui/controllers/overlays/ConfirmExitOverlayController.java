package me.logicologist.wordiple.client.gui.controllers.overlays;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import me.logicologist.wordiple.client.gui.controllers.AttachableAdapter;
import me.logicologist.wordiple.client.manager.GUIManager;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmExitOverlayController extends AttachableAdapter {

    @FXML
    public AnchorPane movablePane;

    @FXML
    public Button cancelButton;

    @FXML
    public Button confirmButton;

    private boolean midAction = false;
    private OverlayController overlayController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setAttachment(movablePane);
        movablePane.setLayoutY(-200);
        movablePane.setOpacity(0);
        confirmButton.setOnAction(x -> {
            if (midAction || GUIManager.getInstance() == null) return;
            midAction = true;
            GUIManager.getInstance().stage.getOnCloseRequest().handle(null);
        });

        cancelButton.setOnAction(x -> {
            if (midAction) return;
            midAction = true;
            transitionOut();
        });

        movablePane.setOnKeyReleased(event -> {
            if (midAction) return;
            midAction = true;

            switch (event.getCode()) {
                case ESCAPE:
                    transitionOut();
                    return;
            }
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
            this.movablePane.requestFocus();
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
