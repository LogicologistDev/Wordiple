package me.logicologist.wordiple.client.gui.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public abstract class FadeTransitionAdapter implements Initializable {

    private AnchorPane pane;

    public void setPane(AnchorPane pane) {
        this.pane = pane;
    }

    public void transitionIn() {
        pane.setOpacity(0);
        pane.setLayoutY(pane.getLayoutY() + 100);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400));
        fadeIn.setNode(this.pane);
        fadeIn.setToValue(1);

        final Timeline timelineIn = new Timeline();
        timelineIn.setCycleCount(1);
        final KeyValue kvOut = new KeyValue(this.pane.layoutYProperty(), 0);
        final KeyFrame kfOut = new KeyFrame(Duration.millis(400), kvOut);
        timelineIn.getKeyFrames().add(kfOut);
        timelineIn.setAutoReverse(false);

        fadeIn.play();
        timelineIn.play();
    }

    public void transitionOut(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400));
        fadeOut.setNode(this.pane);
        fadeOut.setToValue(0);

        final Timeline timelineOut = new Timeline();
        timelineOut.setCycleCount(1);
        final KeyValue kvOut = new KeyValue(this.pane.layoutYProperty(), 100);
        final KeyFrame kfOut = new KeyFrame(Duration.millis(400), kvOut);
        timelineOut.getKeyFrames().add(kfOut);
        timelineOut.setAutoReverse(false);

        timelineOut.play();
        fadeOut.play();

        timelineOut.setOnFinished(event -> {
            onFinished.run();
        });
    }
}
