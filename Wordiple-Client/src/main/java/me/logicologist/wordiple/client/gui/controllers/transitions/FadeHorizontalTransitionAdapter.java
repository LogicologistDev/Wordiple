package me.logicologist.wordiple.client.gui.controllers.transitions;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public abstract class FadeHorizontalTransitionAdapter implements Initializable {

    private AnchorPane pane;

    public void setPane(AnchorPane pane) {
        this.pane = pane;
    }

    public void transitionIn(Runnable runnable) {
        pane.setOpacity(0);
        pane.setLayoutX(pane.getLayoutX() + 100);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400));
        fadeIn.setNode(this.pane);
        fadeIn.setToValue(1);

        final Timeline timelineIn = new Timeline();
        timelineIn.setCycleCount(1);
        final KeyValue kvOut = new KeyValue(this.pane.layoutXProperty(), 0);
        final KeyFrame kfOut = new KeyFrame(Duration.millis(400), kvOut);
        timelineIn.getKeyFrames().add(kfOut);
        timelineIn.setAutoReverse(false);

        fadeIn.play();
        timelineIn.play();
        timelineIn.setOnFinished(event -> {
            if (runnable != null) runnable.run();
        });
    }

    public void transitionIn() {
        transitionIn(null);
    }


    public void transitionOut(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400));
        fadeOut.setNode(this.pane);
        fadeOut.setToValue(0);

        final Timeline timelineOut = new Timeline();
        timelineOut.setCycleCount(1);
        final KeyValue kvOut = new KeyValue(this.pane.layoutXProperty(), 100);
        final KeyFrame kfOut = new KeyFrame(Duration.millis(400), kvOut);
        timelineOut.getKeyFrames().add(kfOut);
        timelineOut.setAutoReverse(false);

        timelineOut.play();
        fadeOut.play();

        timelineOut.setOnFinished(event -> {
            if (onFinished != null) onFinished.run();
        });
    }
}
