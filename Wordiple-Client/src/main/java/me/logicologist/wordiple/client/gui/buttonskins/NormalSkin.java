package me.logicologist.wordiple.client.gui.buttonskins;

import javafx.animation.*;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.util.Duration;


public class NormalSkin extends ButtonSkin {

    public NormalSkin(Button button) {
        super(button);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(100));
        fadeIn.setNode(button);
        fadeIn.setToValue(1);

        final Timeline timelineIn = new Timeline();
        timelineIn.setCycleCount(1);
        final KeyValue kvIn = new KeyValue(button.layoutYProperty(), button.getLayoutY() + 10);
        final KeyFrame kfIn = new KeyFrame(Duration.millis(100), kvIn);
        timelineIn.getKeyFrames().add(kfIn);
        timelineIn.setAutoReverse(false);

        button.setOnMouseEntered(e -> {
            fadeIn.playFromStart();
            timelineIn.play();
        });


        FadeTransition fadeOut = new FadeTransition(Duration.millis(100));
        fadeOut.setNode(button);
        fadeOut.setToValue(0.9);

        button.setOnMouseExited(e -> {
            fadeOut.playFromStart();
            timelineIn.stop();
        });

        button.setOpacity(0.8);
    }


}
