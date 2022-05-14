package me.logicologist.wordiple.client.gui.buttonskins;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import me.logicologist.wordiple.client.manager.SoundManager;
import me.logicologist.wordiple.client.sound.SoundType;

/**
 *  This class is used to animate a rightwards moving button that is not Base-Locked.
 * A Base-Locked button is a button that does not have extra room to move around on screen.
 *
 * @author      Logicologist
 * @since       1.0
 */
public class RightSkin extends ButtonSkin {

    /**
     * Constructor for the RightSkin class.
     *
     * Upon instantiation, the button's animations are automatically set.
     *
     * @param button The button to be animated.
     */
    public RightSkin(Button button) {
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

        FadeTransition fadePressToOut = new FadeTransition(Duration.millis(150));
        fadePressToOut.setNode(button);
        fadePressToOut.setToValue(0.8);

        FadeTransition fadePressToIn = new FadeTransition(Duration.millis(130));
        fadePressToIn.setNode(button);
        fadePressToIn.setToValue(0.9);

        final Timeline timelineOut = new Timeline();
        timelineOut.setCycleCount(1);
        final KeyValue kvOut = new KeyValue(button.layoutXProperty(), button.getLayoutX());
        final KeyFrame kfOut = new KeyFrame(Duration.millis(50), kvOut);
        timelineOut.getKeyFrames().add(kfOut);
        timelineOut.setAutoReverse(false);

        final Timeline timelineIn = new Timeline();
        timelineIn.setCycleCount(1);
        final KeyValue kvIn = new KeyValue(button.layoutXProperty(), button.getLayoutX() + 20);
        final KeyFrame kfIn = new KeyFrame(Duration.millis(25), kvIn);
        timelineIn.getKeyFrames().add(kfIn);
        timelineIn.setAutoReverse(false);

        final Timeline timelinePress = new Timeline();
        timelinePress.setCycleCount(1);
        final KeyValue kvPress = new KeyValue(button.layoutXProperty(), button.getLayoutX() + 80);
        final KeyFrame kfPress = new KeyFrame(Duration.millis(25), kvPress);
        timelinePress.getKeyFrames().add(kfPress);
        timelinePress.setAutoReverse(false);

        final Timeline timelinePressToOut = new Timeline();
        timelinePressToOut.setCycleCount(1);
        final KeyValue kvPressToOut = new KeyValue(button.layoutXProperty(), button.getLayoutX());
        final KeyFrame kfPressToOut = new KeyFrame(Duration.millis(150), kvPressToOut);
        timelinePressToOut.getKeyFrames().add(kfPressToOut);
        timelinePressToOut.setAutoReverse(false);

        final Timeline timelinePressToIn = new Timeline();
        timelinePressToOut.setCycleCount(1);
        final KeyValue kvPressToIn = new KeyValue(button.layoutXProperty(), button.getLayoutX() + 20);
        final KeyFrame kfPressToIn = new KeyFrame(Duration.millis(130), kvPressToIn);
        timelinePressToIn.getKeyFrames().add(kfPressToIn);
        timelinePressToIn.setAutoReverse(false);

        button.setOnMouseEntered(e -> {
            if (button.isPressed()) return;
            fadeIn.playFromStart();
            timelineIn.play();
            timelineOut.stop();
        });

        button.setOnMouseExited(e -> {
            if (button.isPressed()) return;
            timelineIn.stop();
            fadeIn.stop();
            timelinePressToIn.stop();
            fadePressToIn.stop();
            fadeOut.play();
            timelineOut.play();
        });

        button.setOnMousePressed(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            timelinePress.play();
            fadePress.playFromStart();
        });

        button.setOnMouseReleased(e -> {
            timelinePressToOut.stop();
            if (button.isHover()) {
                timelinePressToIn.play();
                fadePressToIn.play();
                if (e.getButton() == MouseButton.PRIMARY) SoundManager.getInstance().playSound(SoundType.BUTTON_CLICK);
                return;
            }
            timelinePressToOut.play();
            fadePressToOut.play();
        });

        button.setOpacity(0.8);
    }
}
