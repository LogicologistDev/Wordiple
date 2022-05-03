package me.logicologist.wordiple.client.gui.buttonskins;

import javafx.animation.*;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.util.Duration;


public class NormalSkin extends ButtonSkin {

    private boolean pressed;

    private boolean exited;
    public NormalSkin(Button button) {
        super(button);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(25));
        fadeIn.setNode(button);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(100));
        fadeOut.setNode(button);
        fadeOut.setToValue(0.9);

        final Timeline timelineOut = new Timeline();
        timelineOut.setCycleCount(1);
        final KeyValue kvOut = new KeyValue(button.layoutYProperty(), button.getLayoutY());
        final KeyFrame kfOut = new KeyFrame(Duration.millis(25), kvOut);
        timelineOut.getKeyFrames().add(kfOut);
        timelineOut.setAutoReverse(false);

        final Timeline timelineIn = new Timeline();
        timelineIn.setCycleCount(1);
        final KeyValue kvIn = new KeyValue(button.layoutYProperty(), button.getLayoutY() + 5);
        final KeyFrame kfIn = new KeyFrame(Duration.millis(25), kvIn);
        timelineIn.getKeyFrames().add(kfIn);
        timelineIn.setAutoReverse(false);

        final Timeline timelinePress = new Timeline();
        timelinePress.setCycleCount(1);
        final KeyValue kvPress = new KeyValue(button.layoutYProperty(), button.getLayoutY() + 20);
        final KeyFrame kfPress = new KeyFrame(Duration.millis(25), kvPress);
        timelinePress.getKeyFrames().add(kfPress);
        timelinePress.setAutoReverse(false);

        button.setOnMouseEntered(e -> {
            System.out.println(button.getLayoutY());
            fadeIn.playFromStart();
            timelineIn.play();
            timelineOut.stop();
            exited = false;
        });

        button.setOnMouseExited(e -> {
            fadeOut.playFromStart();
            exited = true;
            if (pressed) return;
            timelineIn.stop();
            timelineOut.play();
        });

        button.setOnMousePressed(e -> {
            timelinePress.play();
            pressed = true;
        });

        button.setOnMouseReleased(e -> {
            timelinePress.stop();
            pressed = false;
            if (!exited) timelineIn.play();
            if (exited) timelineOut.play();
        });

        button.setOpacity(0.8);
    }


}
