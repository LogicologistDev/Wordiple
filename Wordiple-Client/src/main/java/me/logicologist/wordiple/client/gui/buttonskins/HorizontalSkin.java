package me.logicologist.wordiple.client.gui.buttonskins;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.util.Duration;

public class HorizontalSkin extends ButtonSkin {

    private boolean pressed;

    private boolean exited;

    public HorizontalSkin(Button button) {
        super(button);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(25));
        fadeIn.setNode(button);
        fadeIn.setToValue(0.9);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(50));
        fadeOut.setNode(button);
        fadeOut.setToValue(0.8);

        FadeTransition fadePress = new FadeTransition(Duration.millis(10));
        fadePress.setNode(button);
        fadePress.setToValue(1);

        final Timeline timelineOut = new Timeline();
        timelineOut.setCycleCount(1);
        final KeyValue kvOut = new KeyValue(button.layoutXProperty(), button.getLayoutX());
        final KeyFrame kfOut = new KeyFrame(Duration.millis(50), kvOut);
        timelineOut.getKeyFrames().add(kfOut);
        timelineOut.setAutoReverse(false);

        final Timeline timelineIn = new Timeline();
        timelineIn.setCycleCount(1);
        final KeyValue kvIn = new KeyValue(button.layoutXProperty(), button.getLayoutX() - 20);
        final KeyFrame kfIn = new KeyFrame(Duration.millis(25), kvIn);
        timelineIn.getKeyFrames().add(kfIn);
        timelineIn.setAutoReverse(false);

        final Timeline timelinePress = new Timeline();
        timelinePress.setCycleCount(1);
        final KeyValue kvPress = new KeyValue(button.layoutXProperty(), button.getLayoutX() - 80);
        final KeyFrame kfPress = new KeyFrame(Duration.millis(25), kvPress);
        timelinePress.getKeyFrames().add(kfPress);
        timelinePress.setAutoReverse(false);

        button.setOnMouseEntered(e -> {
            fadeIn.playFromStart();
            timelineIn.play();
            timelineOut.stop();
            exited = false;
        });

        button.setOnMouseExited(e -> {
            exited = true;
            if (pressed) return;
            fadeOut.playFromStart();
            timelineIn.stop();
            timelineOut.play();
        });

        button.setOnMousePressed(e -> {
            timelinePress.play();
            fadePress.playFromStart();
            pressed = true;
        });

        button.setOnMouseReleased(e -> {
            timelinePress.stop();
            pressed = false;
            if (!exited) {
                timelineIn.play();
                fadeIn.play();
                return;
            }
            timelineOut.play();
            fadeOut.play();
        });

        button.setOpacity(0.8);
    }
}
