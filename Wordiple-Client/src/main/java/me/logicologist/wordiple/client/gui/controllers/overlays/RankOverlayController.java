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

public class RankOverlayController extends AttachableAdapter {

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
        movablePane.setOnKeyReleased(event -> {
            if (midAction) return;

            switch (event.getCode()) {
                case ESCAPE:
                    midAction = true;
                    transitionOut();
                    return;
            }
        });
    }

    public void transitionIn(OverlayController overlayController, Runnable runAfter) {
        this.overlayController = overlayController;
        midAction = true;
        double duration = 1;

        movablePane.setLayoutX(-100);
        movablePane.setLayoutY(-500);
        movablePane.setRotate(-25);
        movablePane.setOpacity(0);


        Timeline betterTimeline = new Timeline();
        for (int i = 0; i <= 100; i++) {
            if (i == 100) {
                betterTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration),
                        new KeyValue(movablePane.layoutXProperty(), 0),
                        new KeyValue(movablePane.layoutYProperty(), 0),
                        new KeyValue(movablePane.rotateProperty(), 0),
                        new KeyValue(movablePane.opacityProperty(), 1))
                );
                continue;
            }
            double layoutX = -1 * Math.pow(2, 7 * (i * 0.01 - 1)) * 100;
            double layoutY = -1 * Math.pow(2, 7 * (i * 0.01 - 1)) * 500;
            double rotate = -1 * Math.pow(2, 7 * (i * 0.01 - 1)) * 25;
            double opacity = -1 * Math.pow(2, 7 * (i * 0.01 - 1)) * 1 + 1;

            System.out.println(duration - duration / 100 * i + " - " + layoutX + " | " + layoutY + " | " + rotate + " | " + opacity);

            betterTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( duration - duration / 100 * i),
                    new KeyValue(movablePane.layoutXProperty(), layoutX),
                    new KeyValue(movablePane.layoutYProperty(), layoutY),
                    new KeyValue(movablePane.rotateProperty(), rotate),
                    new KeyValue(movablePane.opacityProperty(), opacity))
            );
        }

        betterTimeline.play();

        betterTimeline.setOnFinished(x -> {
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
