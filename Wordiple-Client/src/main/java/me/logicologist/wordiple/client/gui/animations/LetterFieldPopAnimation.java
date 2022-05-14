package me.logicologist.wordiple.client.gui.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * This class is used to animate the popping of a letter from the letter field.
 *
 * @author      Logicologist
 * @since       1.0
 */
public class LetterFieldPopAnimation {

    private final Label writableValue;
    private final double duration;

    /**
     * This method is used to animate the popping of a letter from the letter field.
     * @param writableValue The value to modify.
     * @param duration The duration of the animation.
     */
    public LetterFieldPopAnimation(Label writableValue, double duration) {
        this.writableValue = writableValue;
        this.duration = duration;
    }

    /**
     * Plays the animation. The animation uses a speed curve to create a nicer and smoother animation.
     */
    public void play() {
        Timeline popTimeline = new Timeline();
        for (int i = 10; i >= 1; i--) {
            if (i == 1) {
                popTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration), new KeyValue(writableValue.scaleXProperty(), 1)));
                popTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration), new KeyValue(writableValue.scaleYProperty(), 1)));
            }
            double v = Math.pow(2, -7 * (1 - 0.1 * i)) * 0.2 + 1;
            popTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration - duration / 10 * i), new KeyValue(writableValue.scaleXProperty(), v)));
            popTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration - duration / 10 * i), new KeyValue(writableValue.scaleYProperty(), v)));
        }
        popTimeline.play();
    }

}
